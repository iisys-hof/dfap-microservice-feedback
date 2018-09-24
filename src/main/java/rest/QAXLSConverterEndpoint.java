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
import xlsConverter.QAXLSConverter;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@Path("/xls/qa")
public class QAXLSConverterEndpoint {

    @Inject
    @XLogger
    private org.slf4j.Logger logger;

    @Inject
    private XLSController xlsController;


    @GET
    @Produces("application/vnd.openxmlformats-officedocument.spreadsheetml.sheetx")
    public Response generateXLS(@QueryParam("startDate") String startDateStr, @QueryParam("endDate") String endDateStr, @QueryParam("toolId") Long toolId) {
        logger.debug("QAXLSConverterEndpoint.generateXLS");
        logger.debug("startDateStr = [" + startDateStr + "], endDateStr = [" + endDateStr + "], toolId = [" + toolId + "]");

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date endDate;
        Date startDate;
        System.out.println("generateXLSFromOrders");
        byte[] file = new byte[0];
        try {
            startDate = formatter.parse(startDateStr);
            endDate = formatter.parse(endDateStr);
            file = xlsController.getQAXLSForToolInDaterange(startDate, endDate, toolId);
            if (file.length < 2) {
                throw new Exception();
            }


        } catch (Exception e) {
            logger.info("No file created");
            return Response.noContent().build();
        }

        return Response
                .ok(file, MediaType.APPLICATION_OCTET_STREAM)
                .header("content-disposition","attachment; filename = " + xlsController.getFileName())
                .build();

    }

}
