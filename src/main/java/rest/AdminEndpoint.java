/*
 * Copyright 2018 Thomas Winkler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rest;

import com.fasterxml.jackson.core.JsonProcessingException;

import controller.QAController;
import entities.TestInstruction;
import entities.TestInstructionProperty;
import entities.Tool;
import jdk.nashorn.internal.objects.annotations.Getter;
import org.slf4j.Logger;
import utils.QLogger;
import utils.XLogger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/admin")
@RequestScoped
public class AdminEndpoint {

    @Inject
    @XLogger
    private Logger logger;

    @Inject
    private QAController qaController;

    @GET
    @Path("/qa")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TestInstruction> getTestInstructionsForTool(@QueryParam("tool") long toolId) {
        logger.debug("AdminEndpoint.getTestInstructionForTool");
        logger.debug("toolId = [" + toolId + "]");

        if(toolId != 0) {
            List<TestInstruction> tlist= new ArrayList<>();
            tlist.add(qaController.getTestInstructionForToolId(toolId));
            return tlist;
        }
        return qaController.getAllTestInstructions();
    }

    @POST
    @Path("/qa")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeTestInstruction(TestInstruction ti)  {
        logger.debug("AdminEndpoint.storeTestInstruction");
        logger.debug("ti = [" + ti + "]");

        boolean storeOK = this.qaController.storeTestInstruction(ti);

        if(!storeOK)
            return Response.status(Response.Status.BAD_REQUEST).entity("Das Schema mit dem Namen exisitiert bereits!").build();
        else
            return Response.ok().build();
    }

    @PUT
    @Path("/qa/{testInstructionId}/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTestInstructionPropertiesForTestInstruction(@PathParam("testInstructionId") long testInstructionId, List<TestInstructionProperty> testInstructionProperties) {
        logger.debug("AdminEndpoint.updateTestInstructionPropertiesForTestInstruction");
        boolean tIUpdateOK = false;
        if(testInstructionId != 0) {
            logger.debug("testInstructionId = [" + testInstructionId + "], testInstructionProperties = [" + testInstructionProperties + "]");
            tIUpdateOK = qaController.updateTestInstructionProperties(testInstructionProperties, testInstructionId);
        }
        if (tIUpdateOK)
            return Response.ok().build();
        else
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Es existieren bereits Einstellungen. Bitte Seite neu laden.").build();

    }

    @PUT
    @Path("/qa/{testInstructionId}/tools")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateToolsForTestInstruction(@PathParam("testInstructionId") long testInstructionId, String tools) {
        logger.debug("AdminEndpoint.updateToolsForTestInstruction");
        logger.debug("testInstructionId = [" + testInstructionId + "], tools = [" + tools + "]");
        boolean toolNamesOK = qaController.updateToolsForTestInstruction(testInstructionId, tools);

        if(toolNamesOK)
            return Response.ok().build();
        else {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("Werkzeugname hat die falsche Länge").build();
        }
    }

    @GET
    @Path("/tools")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> getAllTools() {
        logger.debug("AdminEndpoint.getAllTools");
        return this.qaController.getAllTools();
    }

    @GET
    @Path("/tools/base")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Tool> getAllBaseTools() {
        logger.debug("AdminEndpoint.getAllBaseTools");
        return this.qaController.getAllBaseTools();
    }

    @POST
    @Path("/tools")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTool(Tool tool) throws JsonProcessingException {
        logger.debug("AdminEndpoint.createTool");
        logger.debug("tool = [" + tool + "]");

        this.qaController.storeTool(tool);

        return Response.ok().build();
    }


}
