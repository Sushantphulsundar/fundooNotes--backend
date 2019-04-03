package com.bridgelabz.fundooNotes.label.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.bridgelabz.fundooNotes.label.dto.LabelDto;
import com.bridgelabz.fundooNotes.label.model.Label;
import com.bridgelabz.fundooNotes.label.repository.LabelRepository;
import com.bridgelabz.fundooNotes.note.model.Note;
import com.bridgelabz.fundooNotes.note.repository.NoteRepository;
import com.bridgelabz.fundooNotes.user.exception.UserException;
import com.bridgelabz.fundooNotes.user.model.User;
import com.bridgelabz.fundooNotes.user.repository.IUserRepository;
import com.bridgelabz.fundooNotes.user.response.ResponseSender;
import com.bridgelabz.fundooNotes.user.response.UserResponse;
import com.bridgelabz.fundooNotes.utility.TokenUtil;

@Service
public class LabelServicesImpl implements LabelServices
{
	@Autowired
	IUserRepository userRepository;
	
	@Autowired
	LabelRepository labelRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	Environment environment;
	
	@Autowired
	NoteRepository noteRepository;
	
	public ResponseEntity<UserResponse> createLabel( LabelDto labelDto, String token)
	{
		System.out.println("******");
		Label label = modelMapper.map(labelDto, Label.class);
		
		Long userId = TokenUtil.decodeToken(token);
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(environment.getProperty("user.search")));
		
		labelRepository.save(label);

		user.getLabel().add(label);
	
		//save user
		userRepository.save(user);

		return ResponseSender.sendUserResponse("label is created", 200);
	}
	
	public ResponseEntity<UserResponse> updateLabel( Long labelId, LabelDto labelDto, String token) // UserException2 
	{
		Long userId = TokenUtil.decodeToken(token);
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(environment.getProperty("user.search")));
	    Set<Label> label=user.getLabel();

	    Label filteredlabel = label.stream().filter(data -> data.getLabelId().equals(labelId)).findFirst().orElseThrow(() -> new UserException(404, environment.getProperty("105")));
	    filteredlabel.setLabelTitle(labelDto.getLabelTitle());
	    
		label.add(filteredlabel);
		user.setLabel(label);
		
		userRepository.save(user);

		return ResponseSender.sendUserResponse("Note is created", 200);
	}
	
	public ResponseEntity<UserResponse> deleteLabel( Long labelId, String token)
	{
		Long userId = TokenUtil.decodeToken(token);
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(environment.getProperty("user.search")));
	    Set<Label> label=user.getLabel();

	    Label filteredlabel = label.stream().filter(data -> data.getLabelId().equals(labelId)).findFirst().orElseThrow(() -> new UserException(404, environment.getProperty("105")));
		
	    labelRepository.deleteById(filteredlabel.getLabelId());
	    return ResponseSender.sendUserResponse("Label Deleted successfully", 200);
	} 
	
	public ResponseEntity<UserResponse> addLabelToNote( Long noteId,String labelTitle, String token)
	{
		Long userId = TokenUtil.decodeToken(token);
		
		 LabelDto labelDto = new LabelDto();
		 labelDto.setLabelTitle(labelTitle);
		 
		 User user = userRepository.findById(userId).orElseThrow(() -> new UserException(environment.getProperty("user.search")));
			
		 //Optional<Label> label= user.getLabel().stream().filter(data -> data.getLabelTitle().equals(labelDto.getLabelTitle())).findFirst();
		 Label label= user.getLabel().stream().filter(data -> data.getLabelTitle().equals(labelDto.getLabelTitle())).findFirst().orElse(modelMapper.map(labelDto, Label.class));
		 
		  //to check wheather label is in user object or nor,if it is not present add to user object
		  if(user.getLabel().stream().filter(data -> data.getLabelTitle().equals(labelDto.getLabelTitle())).findFirst().isPresent()==false)
		  {
			  System.out.println("false");
			  labelRepository.save(label);
			  
			  user.getLabel().add(label);
			  userRepository.save(user);
		  }
		  else
		  {
			  System.out.println("label is already present");
		  }
		 
		  Note note=user.getNote().stream().filter(data -> data.getNoteId().equals(noteId)).findFirst().orElseThrow(()-> new UserException(environment.getProperty("user.search")));
		  System.out.println(note);
		  
		  if( note.getLabel().stream().filter(data-> data.getLabelTitle().equals(labelTitle)).findFirst().isPresent()==false)
		  {
			  note.getLabel().add(label);
			  noteRepository.save(note);
			  System.out.println("label add to note***");
		  }
		  return ResponseSender.sendUserResponse("Label added to note successfully", 200);
	} 
	
	public ResponseEntity<UserResponse> deleteLabelFromNote( Long labelId, String token) {
		return null;
	} 
	
	public Set<Label> getAllLabels( String token)
	{
		Long userId = TokenUtil.decodeToken(token);
		
		User user = userRepository.findById(userId).orElseThrow(() -> new UserException(environment.getProperty("user.search")));
		Set<Label> label=user.getLabel();
	    
		System.out.println(label);
		return label;
	} 
//==================================
//	public List<Label> getAllLabels(@RequestHeader String token)
//	{
//		
//		return labelRepository.findAll();
//	}

	
//	public ResponseEntity<Response> deleteLabel(Long labelid) {
//	
//		labelRepository.deleteById(labelid);
//		return ResponseSender.sendResponse(environment.getProperty("003"),200);
//	}

}