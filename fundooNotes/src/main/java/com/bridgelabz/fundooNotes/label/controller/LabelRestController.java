package com.bridgelabz.fundooNotes.label.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bridgelabz.fundooNotes.label.dto.LabelDto;
import com.bridgelabz.fundooNotes.label.model.Label;
import com.bridgelabz.fundooNotes.label.service.LabelServices;
import com.bridgelabz.fundooNotes.note.model.Note;
import com.bridgelabz.fundooNotes.user.response.UserResponse;

@RestController
@RequestMapping("/fundooNotes/label")
@CrossOrigin(origins="http://localhost:4200",allowedHeaders="*",exposedHeaders="jwtToken")
public class LabelRestController 
{
	@Autowired(required=true)
	LabelServices labelServices;
	
	//successfully
	/**
	 * Method to Create a label
	 */
	@PostMapping(path="/createlabel")
	public ResponseEntity<UserResponse> createLabel(@RequestBody LabelDto labelDto,@RequestHeader(value="jwtToken") String jwtToken) 
	{
		return labelServices.createLabel(labelDto, jwtToken);
	}
	
    //successfully
	/**
	 * Method to update a--->particular label
	 */
	@PostMapping(path="/updatelabel") 
	public ResponseEntity<UserResponse> updateNote(@RequestParam("labelId") Long noteId,@RequestBody LabelDto labelDto,@RequestHeader String token) // UserException2 
	{ 
		return labelServices.updateLabel(noteId, labelDto,token);
	}
	
	//successfull
	/**
	 * Method to move a-->particular note to trash 
	 */
	@PostMapping(value="/delete") 
	public ResponseEntity<UserResponse> deleteNote(@RequestParam("labelId") Long labelId,@RequestHeader String token) 
	{
		System.out.println("***************");
		return labelServices.deleteLabel(labelId, token);
	}

	@PostMapping("/labelToNote")
	public ResponseEntity<UserResponse> addLabelToNote(@RequestParam Long noteId,@RequestParam String labelTitle,@RequestHeader String token) 
	{
		System.out.println("***************");
		return labelServices.addLabelToNote(noteId, labelTitle, token);
	}
	
	@PostMapping("/deleteLabel")
	public ResponseEntity<UserResponse> deleteLabelFromNote(@RequestParam Long labelId,@RequestHeader String token) 
	{
		System.out.println("***************");
		return labelServices.deleteLabel(labelId, token);
	}
	
	//successfully
	/**
	 * Method to getAll notes--->from particular id 
	 */
	@GetMapping(value="/getAlls") 
	public Set<Label> getAllNotes(@RequestHeader(value="jwtToken") String token) throws UnsupportedEncodingException
	{
		System.out.println("======");
		return labelServices.getAllLabels(token);
	}
}