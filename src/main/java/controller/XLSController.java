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

package controller;

import daos.*;

import daos.feedback.JPAFeedbackEntryDao;
import daos.machine.JPAMachineDao;
import daos.machine.MachineDao;
import daos.ordering.JPAOrderingDao;
import daos.testInstruction.JPATestInstructionDao;
import daos.testInstruction.JPATestInstructionPropertyDao;
import daos.tool.JPAToolDao;
import daos.tool.ToolDao;
import entities.*;
import xlsConverter.FeedbackXLSConverter;
import xlsConverter.QAXLSConverter;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
@RequestScoped
public class XLSController {



    @Inject
    private JPAOrderingDao dao;
    @Inject
    private JPAToolDao toolDao;

    @Inject
    private JPAFeedbackEntryDao fbDao;

    @Inject
    private FeedbackXLSConverter fbxlsConverter;

    @Inject
    private JPATestInstructionDao testInstructionDao;

    @Inject
    private JPATestInstructionPropertyDao testInstructionPropertyDao;

    @Inject
    private QAXLSConverter qaxlsConverter;

   @Inject
   private JPAMachineDao machineDao;


    private String filename = "empty.xlsx";

    public List<Ordering> getOrdersInDaterange(Date startDate, Date endDate) {
        return dao.getOrdersInDaterange(startDate, endDate);

    }

    public byte[] getXLSForOrdersInDaterange(Date startDate, Date endDate) {
        List<Ordering> orderings = dao.getOrdersInDaterange(startDate, endDate);
        fbxlsConverter.generatetemplate();
        for (Ordering order: orderings) {
             fbxlsConverter.getData(order.getOrderingId());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.filename = "feedbacks-" + dateFormat.format(startDate) + "-" + dateFormat.format(endDate) + ".xlsx";

        return fbxlsConverter.getByteArray();
    }

    public byte[] getXLSFeedbacksInDaterange(Date startDate, Date endDate, boolean isFinished) {
        List<FeedbackEntry> feedbacks;
        if (isFinished) {
            feedbacks = fbDao.getFinishedFeedbacksInDaterange(startDate, endDate);
        } else {
            feedbacks = fbDao.getFeedbacksInDaterange(startDate, endDate);

        }

        fbxlsConverter.generatetemplate();
        fbxlsConverter.getData(feedbacks);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.filename = "feedbacks-" + dateFormat.format(startDate) + "-" + dateFormat.format(endDate) + ".xlsx";

        return fbxlsConverter.getByteArray();
    }

    public byte[] getFeedbackForOrder(long orderId) {
        fbxlsConverter.generatetemplate();
        fbxlsConverter.getData(orderId);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.filename = "feedbacks-" + orderId + "-" + dateFormat.format(new Date()) + ".xlsx";

        return fbxlsConverter.getByteArray();
    }

    public String getFileName() {
        return this.filename;
    }

    /* QA Feedback */

    public byte[] getQAXLSForToolInDaterange(Date startDate, Date endDate, long toolId) {

        List<TestInstructionEntry> entries = this.testInstructionDao.readTestInstructionEntitiesForToolId(startDate, endDate, toolId);

        Tool t = this.toolDao.readToolForId(toolId);
        t = this.toolDao.findToolForNameAndVersion(t.getName(), null);
        try {
            List<TestInstructionProperty> entryNames = t.getTestInstructionId().getTestInstructionPropertyList();


            System.out.println("Entrienames: " + entryNames);
            qaxlsConverter.getData(entries, entryNames);
            qaxlsConverter.generateTemplate();
            qaxlsConverter.fillInDataMN();


            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            this.filename = "qa-" +toolId + "-" + dateFormat.format(startDate) + "-" + dateFormat.format(endDate) + ".xlsx";

            return qaxlsConverter.getByteArray();
        }
        catch (Exception e) {
            return new byte[0];
        }

    }


    public byte[] getXLSFeedbacksInDaterangeForMachine(Date startDate, Date endDate, Long machineId) {
        List<FeedbackEntry> feedbacks;
        Machine m = machineDao.findMachineForId(machineId);
        feedbacks = fbDao.getFeedbacksInDaterangeForMachine(startDate, endDate, machineId);



        fbxlsConverter.generatetemplate();
        fbxlsConverter.getData(feedbacks);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.filename = "feedbacks-" + dateFormat.format(startDate) + "-" + dateFormat.format(endDate) + ".xlsx";

        return fbxlsConverter.getByteArray();

    }
}
