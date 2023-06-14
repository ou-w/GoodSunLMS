package com.reality.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.reality.entity.Attendance;
import com.reality.form.DailyReportForm;
import com.reality.repository.AttendanceRepository;

import jakarta.servlet.http.HttpSession;

public class Form2ExcelMM {
	
	XSSFWorkbook wb;
	XSSFSheet ws;
	
	public void runForm2Excel(List<Attendance> list, String date, HttpSession session) throws Exception {
//		doExcel(list, session);
		buildExcel(list, date, session);
	}
	
	
	private void buildExcel(List<Attendance> list, String date, HttpSession session) throws Exception {
		// excel生成
		
		// template利用
		String templatePath = "excel/ExcelMM_template.xlsx";
		InputStream tmpFile = this.getClass().getResourceAsStream(templatePath);

		wb = new XSSFWorkbook(tmpFile);
		ws = wb.getSheetAt(0);
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		SimpleDateFormat sdfE = new SimpleDateFormat("E");
		SimpleDateFormat fileSdf = new SimpleDateFormat("YYYYMM");

		String yyyy = date.split("/")[0];
		String mm = Integer.parseInt(date.split("/")[1])<10?"0"+date.split("/")[1]:date.split("/")[1];
			
		// Cell処理...
		int row_pos = 4;
		XSSFCellStyle wrapStyle = wb.createCellStyle();
		wrapStyle.setWrapText(true);
		// 日付
		this.setValue(1, 0, yyyy+"年度"+mm+"月度");
		// 氏名
		this.setValue(1, 7, session.getAttribute("fullName").toString());
		for (int i = 0; i < list.size(); i++) {
			int col_pos = 0;
			// 日付
			this.setValue(row_pos, col_pos++, sdf.format(list.get(i).getDate()));
			this.setValue(row_pos, col_pos++, sdfE.format(list.get(i).getDate()));
			// 開始終了
			this.setValue(row_pos, col_pos++, list.get(i).getStartTime());
			this.setValue(row_pos, col_pos++, list.get(i).getEndTime());
			// 区分
			this.setValue(row_pos, col_pos++, list.get(i).getDivision());
			// 時間
			col_pos++;
			// プロジェクト
			this.setValue(row_pos, col_pos++, list.get(i).getProject());
			// 作業場所
			this.setValue(row_pos, col_pos++, list.get(i).getPlace());
			// 備考
			this.setValue(row_pos, col_pos++, list.get(i).getRemarks());

			row_pos++;
		}
		wb.setForceFormulaRecalculation(true);
		// output
		
 		File desktopDir = FileSystemView.getFileSystemView().getHomeDirectory();
		String outputFilePath = desktopDir.getAbsolutePath()+"\\";
		System.out.println(fileSdf.format(new Date()));
		String outputFileName = yyyy+mm+"_月報_"+ session.getAttribute("fullName").toString() + ".xlsx";
		Files.createDirectories(new File(outputFilePath).toPath());
		FileOutputStream stream = new FileOutputStream(outputFilePath + outputFileName);
		wb.write(stream);
		stream.close();

		wb.close();

		System.out.println("JOB_DONE");
	}
	
	private void setValue(int row_pos, int col_pos, Object value) throws Exception {
		// create and set cell
		if (ws.getRow(row_pos) == null) {
			ws.createRow(row_pos);
		}
		if (ws.getRow(row_pos).getCell(col_pos) == null) {
			XSSFCell newCell = ws.getRow(row_pos).createCell(col_pos);
			try {
				ws.getRow(row_pos).getCell(col_pos).getCellStyle().setWrapText(true);
				newCell.setCellStyle(ws.getRow(row_pos).getCell(col_pos).getCellStyle());
			} catch (Exception ex) {
				String exm = ex.getMessage();
				System.out.println(exm);
				System.out.println(ex.getStackTrace());
			}
		}

		// if NULL
		if (value == null) {
//			System.err.println("Value is null");
			value = "";
			return;
		}
		String className = value.getClass().getName();
//		System.out.println(value + " is " + className);

		if (className == "java.lang.String") {
			ws.getRow(row_pos).getCell(col_pos).setCellValue((String) value);
		} else {
			throw new Exception("Cell format not supported: " + className);
		}		
	}
}