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

import controller.XLSController;
import org.apache.log4j.Logger;
import utils.QLogger;
import utils.XLogger;
import xlsConverter.FeedbackXLSConverter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Path("xls/feedback")
@RequestScoped
public class FeedbackXLSConverterEndpoint {
    @Inject
    @XLogger
    private org.slf4j.Logger logger;


    @Inject
    private XLSController xlsController;

    @GET
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheetx")
    @Path("/{orderId}")
    public Response generateXLS(@PathParam("orderId") Long orderId) throws IOException {
        logger.debug("FeedbackXLSConverterEndpoint.generateXLS");
        logger.debug("orderId = [" + orderId + "]");
        byte[] file = xlsController.getFeedbackForOrder(orderId);

        return Response
                .ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = " + xlsController.getFileName())
                .build();

    }

    @GET
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheetx")
    public Response generateXLSFromOrders(@QueryParam("startDate") String startDateStr, @QueryParam("endDate") String endDateStr, @QueryParam("isFinished") boolean isFinished, @QueryParam("machineId") Long machineId) {
        logger.debug("FeedbackXLSConverterEndpoint.generateXLSFromOrders");
        logger.debug("startDateStr = [" + startDateStr + "], endDateStr = [" + endDateStr + "], isFinished = [" + isFinished + "], machineId = [" + machineId + "]");
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date endDate;
        Date startDate;
        System.out.println("generateXLSFromOrders");
        byte[] file = new byte[0];
        try {

            startDate = formatter.parse(startDateStr);
            endDate = formatter.parse(endDateStr);
            if (machineId != null)  {
                file = xlsController.getXLSFeedbacksInDaterangeForMachine(startDate, endDate, machineId);
            } else {
                file = xlsController.getXLSFeedbacksInDaterange(startDate, endDate, isFinished);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }



       return Response
                .ok(file, MediaType.APPLICATION_OCTET_STREAM)
               .header("content-disposition","attachment; filename = " + xlsController.getFileName())
               .build();



    }

}

