package com.aermias.endpoints;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.aermias.entity.Actor;
import com.aermias.generated_classes.ActorInformation;
import com.aermias.generated_classes.AddActorRequest;
import com.aermias.generated_classes.AddActorResponse;
import com.aermias.generated_classes.DeleteActorRequest;
import com.aermias.generated_classes.DeleteActorResponse;
import com.aermias.generated_classes.GetActorByIdRequest;
import com.aermias.generated_classes.GetActorByIdResponse;
import com.aermias.generated_classes.GetAllActorsResponse;
import com.aermias.generated_classes.ServiceStatus;
import com.aermias.generated_classes.UpdateActorRequest;
import com.aermias.generated_classes.UpdateActorResponse;
import com.aermias.service.IActorService;

@Endpoint
public class ActorEndpoint {
	private static final String NAMESPACE_URI = "http://aermias.com/actor-ws";

	private IActorService actorService;
	
	// Autowires constructor
	@Autowired
	public ActorEndpoint(IActorService actorService) {
		this.actorService = actorService;
	}
	
	// creates payload root with designated namespace and local part
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getActorByIdRequest")
	// annotates the following method as a response entity method
	@ResponsePayload
	public GetActorByIdResponse getActor(@RequestPayload GetActorByIdRequest request) {
		GetActorByIdResponse response = new GetActorByIdResponse();
		ActorInformation actorInformation = new ActorInformation();
		
		// populates the empty ActorInformation object with matching properties from the Actor object (from the repository)
		BeanUtils.copyProperties(actorService.getActorById(request.getId()), actorInformation);
		response.setActorInformation(actorInformation);
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAllActorsRequest")
	@ResponsePayload
	public GetAllActorsResponse getAllActors() {
		GetAllActorsResponse response = new GetAllActorsResponse();
		List<ActorInformation> actorInfoList = new ArrayList<>();
		List<Actor> actorList = actorService.getAllActors();
		
		for (int i = 0; i < actorList.size(); i++) {
			ActorInformation aI = new ActorInformation();
			BeanUtils.copyProperties(actorList.get(i), aI);
			actorInfoList.add(aI);    
		}
		
		response.getActorInformation().addAll(actorInfoList);
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "addActorRequest")
	@ResponsePayload
	public AddActorResponse addActor(@RequestPayload AddActorRequest request) {
		AddActorResponse response = new AddActorResponse();		
		ServiceStatus serviceStatus = new ServiceStatus();		
		Actor actor = new Actor();
		
		actor.setName(request.getName());
		
		boolean flag = actorService.addActor(actor);
		
		if (flag == false) {
			serviceStatus.setStatusCode("CONFLICT");
			serviceStatus.setMessage("Content Already Available");
			response.setServiceStatus(serviceStatus);
		} else {
			ActorInformation actorInformation = new ActorInformation();
			BeanUtils.copyProperties(actor, actorInformation);
			response.setActorInformation(actorInformation);
			serviceStatus.setStatusCode("SUCCESS");
			serviceStatus.setMessage("Content Added Successfully");
			response.setServiceStatus(serviceStatus);
		}
		
		return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "updateActorRequest")
	@ResponsePayload
	public UpdateActorResponse updateActor(@RequestPayload UpdateActorRequest request) {
		Actor actor = new Actor();
		BeanUtils.copyProperties(request.getActorInformation(), actor);
		actorService.updateActor(actor);

		ServiceStatus serviceStatus = new ServiceStatus();
		serviceStatus.setStatusCode("SUCCESS");
    	serviceStatus.setMessage("Content Updated Successfully");
    	
    	UpdateActorResponse response = new UpdateActorResponse();
    	response.setServiceStatus(serviceStatus);
    	
    	return response;
	}
	
	@PayloadRoot(namespace = NAMESPACE_URI, localPart = "deleteActorRequest")
	@ResponsePayload
	public DeleteActorResponse deleteActor(@RequestPayload DeleteActorRequest request) {
		Actor actor = actorService.getActorById(request.getId());
		ServiceStatus serviceStatus = new ServiceStatus();
		
		if (actor == null) {
			serviceStatus.setStatusCode("FAIL");
			serviceStatus.setMessage("Content Not Available");
		} else {
			actorService.deleteActor(actor);
			serviceStatus.setStatusCode("SUCCESS");
			serviceStatus.setMessage("Content Deleted Successfully");
		}
		
		DeleteActorResponse response = new DeleteActorResponse();
		response.setServiceStatus(serviceStatus);
		return response;
	}
}