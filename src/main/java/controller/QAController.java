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


import daos.testInstruction.JPATestInstructionDao;
import daos.testInstruction.JPATestInstructionEntryDao;
import daos.testInstruction.JPATestInstructionPropertyDao;
import daos.tool.JPAToolDao;
import entities.*;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.*;


@RequestScoped
public class QAController {

    @Inject
    private JPATestInstructionDao dao;

    @Inject
    private JPAToolDao toolDao;

    @Inject
    private JPATestInstructionEntryDao testInstructionEntryDao;

    @Inject
    private JPATestInstructionPropertyDao testInstructionPropertyDao;


    public TestInstruction getTestInstructionForToolId(long toolId){

        return dao.readTestInstructionForToolId(toolId);
    }

    public List<TestInstructionEntry> getTestInstructionEntryList(long toolId){

        return dao.readTestInstructionEntitiesForToolId(toolId);
    }

    public List<TestInstructionProperty> getTestInstructionPropertiesForToolId(int toolId) {

        Tool t = this.toolDao.readToolForId(toolId);
        List<TestInstructionProperty> entryNames = t.getTestInstructionId().getTestInstructionPropertyList();

        return entryNames;
    }

    public void storeTestInstructionEntryForTool(TestInstructionEntry tie, long toolId) {
        TestInstruction ti = dao.readTestInstructionForToolId(toolId);
        if(ti != null) {
            tie.setTestInstructionId(ti);
            for (TestInstructionValue tiv: tie.getTestInstructionValueList()) {
                tiv.setTestInstructionEntryId(tie);
            }
            dao.updateTestInstruction(ti, tie);
        }

    }

    public void updateTestInstructionEntries(List<TestInstructionEntry> ties) {
        for (TestInstructionEntry tie: ties) {
            testInstructionEntryDao.updateTestInstructionEntry(tie);
        }
    }

    public List<Tool> getAllTools(){
        return this.toolDao.readAllTools();
    }

    public List<Tool> getAllBaseTools(){
        return this.toolDao.readAllBaseTools();
    }

    public void storeTool(Tool tool) {
        this.toolDao.storeTool(tool);
    }

    public void updateTestInstruction(TestInstruction testInstruction) {
        this.dao.updateTestInstruction(testInstruction);
    }

    public boolean updateTestInstructionProperties(List<TestInstructionProperty> testInstructionProperties, long testInstructionId) {
       TestInstruction ti =  dao.readTestInstruction(testInstructionId);
        System.out.println("TTTT" + testInstructionProperties.get(0).getTestInstructionPropertyId());
        System.out.println("TTTx" + ti.getTestInstructionPropertyList());
       if (testInstructionProperties.get(0).getTestInstructionPropertyId() == null && ti.getTestInstructionPropertyList().size() != 0) {
           return false;
       }
        for (TestInstructionProperty tip: testInstructionProperties) {
            tip.setTestInstructionId(ti);
        }

        ti.setTestInstructionPropertyList(testInstructionProperties);
        dao.updateTestInstruction(ti);
        return true;
    }

    public boolean storeTestInstruction(TestInstruction ti) {
        if(dao.readTestInstructionForName(ti.getName()) != null) {
            return false;
        }
        if(ti.getTestInstructionPropertyList()!= null)
            for (TestInstructionProperty tip: ti.getTestInstructionPropertyList()) {
                tip.setTestInstructionId(ti);
            }
        if(ti.getTestInstructionEntryList()!= null)
            for (TestInstructionEntry tie: ti.getTestInstructionEntryList()) {
                tie.setTestInstructionId(ti);
            }
        dao.storeTestInstruction(ti);
        return true;
    }

    public void storeTestInstructionEntryForTool(long toolId) {
        Tool t  = toolDao.readToolForId(toolId);
        TestInstruction ti = dao.readTestInstructionForToolId(toolId);

        if(ti == null) {
            System.out.println("noch nix vorhanden fpr tool, daher tool ohen variante suchen und dem toll die ti zusweisej");
            Tool baseTool = toolDao.findToolForNameAndVersion(t.getName(), null);
            if(baseTool != null) {
                TestInstruction baseTi =  dao.readTestInstructionForToolId(baseTool.getToolId());
                if(baseTi != null) {
                    //toolDao.updateTool(t, baseTi); // nicht die eigentliche Variante mit der TI verbinden, weil nur die base version damit verknüpft sein soll
                    ti = baseTi;
                }
            }

        }
        System.out.println(ti);
        TestInstructionEntry tie = new TestInstructionEntry();
        tie.setDate(new Date());
        tie.setTestInstructionId(ti);
        tie.setToolId(t);

        List<TestInstructionValue> tivList = new ArrayList<>();

        if(ti != null && ti.getTestInstructionPropertyList().size() > 0) {
            for (TestInstructionProperty tip: ti.getTestInstructionPropertyList()) {
                TestInstructionValue tiv = new TestInstructionValue();
                tiv.setTestInstructionEntryId(tie);
                tiv.setNumber(tip.getNumber());
                tivList.add(tiv);
            }
            tie.setTestInstructionValueList(tivList);

            if(ti.getTestInstructionEntryList() == null)
                ti.setTestInstructionEntryList(new ArrayList<>());
            ti.getTestInstructionEntryList().add(tie);

            dao.updateTestInstruction(ti);
        }


    }

    public List<TestInstruction> getAllTestInstructions() {
        return dao.readAllTestInstructions();
    }

    public boolean updateToolsForTestInstruction(long testInstructionId, String toolString) {
        TestInstruction ti = this.dao.readTestInstruction(testInstructionId);
        String [] toolNames = toolString.split(",");
        List<String> toolNamesList = new ArrayList<>(Arrays.asList(toolNames));

        toolNamesList.removeIf(toolName -> toolName.trim().length() == 0);

        for (String toolName: toolNamesList) {
          if (toolName.trim().length() != 6){
                return false;
            }
        }


        for (String aToolNamesList : toolNamesList) {

            Tool t = toolDao.findToolForNameAndVersion("W" + aToolNamesList.trim(), null);
            System.out.println(t);
            if (t == null) {
                Tool tnew = new Tool(null, aToolNamesList.trim());
                t = toolDao.storeTool(tnew);
            }
            toolDao.updateTool(t, ti);

        }
        return true;
    }

    public TestInstruction getTestInstructionForToolName(String toolName) {
        Tool t = toolDao.findToolForNameAndVersion(toolName, null);
        if( t != null)
            return dao.readTestInstructionForToolId(t.getToolId());
        else return null;
    }
}
