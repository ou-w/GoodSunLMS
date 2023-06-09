package com.reality.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import com.reality.entity.Attendance;
import com.reality.entity.User;
import com.reality.repository.AttendanceRepository;
import com.reality.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
@Controller
public class ComplaintsController {
	@Autowired
	AttendanceRepository attendanceRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@PostMapping("/complaints")
	public String complaints(Model model, HttpSession session, String startTime, String endTime, String place) {
		Attendance attendance = new Attendance();
		User user = userRepository.findByUserName(session.getAttribute("userName").toString());
		Date date = new Date();
		attendance.setDate(date);
		attendance.setStartTime(startTime);
		attendance.setEndTime(endTime);
		attendance.setWorkHours("0:30");
		attendance.setPlace(place);
		attendance.setProject("新人教育（愚痴相談）");
		attendance.setUser(user);
		attendanceRepository.save(attendance);
		model.addAttribute("attendance");
		return "complaintsMessage";
	}
}
