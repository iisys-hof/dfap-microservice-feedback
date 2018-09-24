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
import daos.feedback.JPAFeedbackEntryDao;
import entities.FeedbackEntry;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;
import utils.QLogger;
import utils.XLogger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/feedback")
@RequestScoped
public class FeedbackEndpoint {

    @Inject
    @XLogger
    private org.slf4j.Logger logger;


    @Inject
    private JPAFeedbackEntryDao dao;


    private ObjectMapper objectMapper = new ObjectMapper();

    private List<FeedbackEntry> getAllFeedbacks() {
        logger.debug("FeedbackEndpoint.getAllFeedbacks");
        List<FeedbackEntry> x = dao.readAllFeedbacks();
        return x;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getFeedback(@QueryParam("order") long orderId) throws JsonProcessingException {
        logger.debug("FeedbackEndpoint.getFeedback");
        logger.debug("orderId = [" + orderId + "]");
        if(orderId != 0) {
            List<FeedbackEntry> x = dao.getFeedbackForOrder(orderId);
            return objectMapper.writeValueAsString(x);
        }
        else {
            return objectMapper.writeValueAsString(this.getAllFeedbacks());
        }
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String storeFeedbackForOrder(List<FeedbackEntry> l, @QueryParam("order") long orderId) throws JsonProcessingException {
        if(orderId != 0) {
            logger.debug("FeedbackEndpoint.storeFeedbackForOrder");
            logger.debug("l = [" + l + "], orderId = [" + orderId + "]");
            dao.storeFeedbackList(l, orderId);

        }
        return this.getFeedback(orderId);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void updateFeedback(FeedbackEntry l) {
        logger.debug("FeedbackEndpoint.updateFeedback");
        logger.debug("l = [" + l + "]");
        dao.updateFeedback(l);
    }

    @DELETE
    @Path("{feedbackEntryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public void deleteFeedback(@PathParam("feedbackEntryId") Long feedbackEntryId) {
        logger.debug("FeedbackEndpoint.deleteFeedback");
        logger.debug("feedbackEntryId = [" + feedbackEntryId + "]");
        dao.deleteFeedback(feedbackEntryId);
    }

}

