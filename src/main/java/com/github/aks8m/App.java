package com.github.aks8m;

import com.google.common.collect.Lists;
import net.sf.mpxj.CustomField;
import net.sf.mpxj.MPXJException;
import net.sf.mpxj.ProjectFile;
import net.sf.mpxj.Task;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.reader.ProjectReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * aks8m - 8/20/2018
 *
 */
public class App 
{

    private static List<WritableTask> writableTasks = new ArrayList<>();

    public static void main( String[] args )
    {
        try {
            ProjectReader projectReader = new MPPReader();
            ProjectFile projectFile = projectReader.read(args[0]);

            List<Task> tasks = Lists.newArrayList(projectFile.getTasks().iterator());
            List<CustomField> customFields = Lists.newArrayList(projectFile.getCustomFields().iterator());

            for(Task task : tasks){

                Task parentTask = task.getParentTask();

                if(parentTask != null){
                    if(parentTask.getName().contains("C:") || parentTask.getName().contains("VHIE IMS")){
                        writableTasks.add( new WritableTask(task));
                        depthFirstSearch(task, customFields);
                    }
                }
            }

        }catch (MPXJException mpxjE){
            mpxjE.printStackTrace();
        }

        writeExcelFile(args[1]);
    }

    private static void depthFirstSearch(Task task, List<CustomField> customFields){

        if(task.getChildTasks() != null) {
            for (Task childTask : task.getChildTasks()){
                depthFirstSearch(childTask, customFields);
            }
        }

        if(task.getChildTasks().size() == 0 &&  !writableTasks.stream().anyMatch(writableTask -> writableTask.getWbs().equals(task.getWBS()))){
            WritableTask writableTask = new WritableTask(task, customFields);
            writableTask.buildHierarchy();
            writableTasks.add(writableTask);
        }
    }

    private static void writeExcelFile(String outputFileName){
        HSSFWorkbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("MS Project Extraction");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("WBS");
        header.createCell(1).setCellValue("Program Name");
        header.createCell(2).setCellValue("Project Name");
        header.createCell(3).setCellValue("Task Name");
        header.createCell(4).setCellValue("Task Start Date");
        header.createCell(5).setCellValue("Task Finish Date");
        header.createCell(6).setCellValue("Task Percentage Complete");
        header.createCell(7).setCellValue("Duration");
        header.createCell(8).setCellValue("Milestone");
        header.createCell(9).setCellValue("Deliverable");
        header.createCell(10).setCellValue("Notes");
        header.createCell(11).setCellValue("Resources");


        int rowCount = 1;
        for(WritableTask writableTask: writableTasks){
            Row newRow = sheet.createRow(rowCount);
            newRow.createCell(0).setCellValue(writableTask.getWbs());
            newRow.createCell(1).setCellValue(writableTask.getProgramName());
            newRow.createCell(2).setCellValue(writableTask.getProjectName());
            newRow.createCell(3).setCellValue(writableTask.getName());
            newRow.createCell(4).setCellValue(writableTask.getStartDate());
            newRow.createCell(5).setCellValue(writableTask.getFinishDate());
            newRow.createCell(6).setCellValue(new Double(writableTask.getPercentageComplete()));
            newRow.createCell(7).setCellValue(new Double(writableTask.getDuration()));
            newRow.createCell(8).setCellValue(writableTask.isMilestone());
            newRow.createCell(9).setCellValue(writableTask.isDeliverable());
            newRow.createCell(10).setCellValue(writableTask.getNotes());
            newRow.createCell(11).setCellValue(writableTask.getResource());
            rowCount++;
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outputFileName);
            workbook.write(fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException ioE){
            ioE.printStackTrace();
        }
    }
}
