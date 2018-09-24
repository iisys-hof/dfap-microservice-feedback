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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import controller.QAController;
import entities.TestInstruction;
import entities.TestInstructionEntry;
import entities.TestInstructionProperty;
import org.apache.log4j.Logger;
import utils.QLogger;
import utils.XLogger;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.util.List;

@Path("/qa")
@RequestScoped
public class QAEndpoint {

    @Inject
    @XLogger
    private org.slf4j.Logger logger;

    @Inject
    QAController qaController;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestInstruction getTestInstructionForTool(@QueryParam("tool") long toolId, @QueryParam("toolName") String toolName) throws JsonProcessingException {
        logger.debug("QAEndpoint.getTestInstructionForTool");
        logger.debug("toolId = [" + toolId + "], toolName = [" + toolName + "]");

        if(toolId != 0 && toolName != null){
            logger.error("Duplicate values");
            return null;
        }
        if(toolId != 0) {
            return qaController.getTestInstructionForToolId(toolId);
        }
        if(toolName != null) {
            return qaController.getTestInstructionForToolName(toolName);
        }
        return null;
    }

    @GET
    @Path("/entries")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TestInstructionEntry> getTestInstructionEntriesForTool(@QueryParam("tool") long toolId) throws JsonProcessingException {
        logger.debug("QAEndpoint.getTestInstructionEntriesForTool");
        logger.debug("toolId = [" + toolId + "]");
        if(toolId != 0) {
            return qaController.getTestInstructionEntryList(toolId);
        }
        return null;
    }

    @POST
    @Path("/entries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response storeNewTestInstructionEntryForTool(TestInstructionEntry e, @QueryParam("tool") long toolId) throws IOException {
        logger.debug("QAEndpoint.storeNewTestInstructionEntryForTool");
        logger.debug("e = [" + e + "], toolId = [" + toolId + "]");
        if(toolId != 0) {
            if (e != null)
                qaController.storeTestInstructionEntryForTool(e, toolId);
            else
                qaController.storeTestInstructionEntryForTool(toolId);
        }
        return Response.ok().build();
    }

    @PUT
    @Path("/entries")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateTestInstructionEntriesForTool(List<TestInstructionEntry> e, @QueryParam("tool") String toolId) throws IOException {
        logger.debug("QAEndpoint.updateTestInstructionEntriesForTool");
        logger.debug("e = [" + e + "], toolId = [" + toolId + "]");
        if(toolId != null) {
            qaController.updateTestInstructionEntries(e);
        }
        return Response.ok().build();
    }


    @GET
    @Path("/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TestInstructionProperty> getTestInstructionPropertiesForTool(@QueryParam("tool") String toolId) {
        logger.debug("QAEndpoint.getTestInstructionPropertiesForTool");
        logger.debug("toolId = [" + toolId + "]");
        if(toolId != null) {
           return qaController.getTestInstructionPropertiesForToolId(Integer.valueOf(toolId));
        }
        return null;
    }


}
