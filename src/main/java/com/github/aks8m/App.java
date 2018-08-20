package com.github.aks8m;

import com.google.common.collect.Lists;
import net.sf.mpxj.*;
import net.sf.mpxj.mpp.MPPReader;
import net.sf.mpxj.reader.ProjectReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * aks8m - 8/20/2018
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        /**
         * Project Name (2nd level)
         * Milestones (red, italicized) - getMilestone()
         * Deliverables - done
         * Program Name -
         * Task name - getName()
         * Start date - getStart()
         * Finish date - getFinish()
         * Percentage completed - getPercentageComplete()
         * Resource (not as important) - getResourceAssignment().getResource().getName()
         */

        try {
            ProjectReader projectReader = new MPPReader();
            ProjectFile projectFile = projectReader.read(args[0]);

            List<Task> tasks = Lists.newArrayList(projectFile.getTasks().iterator());
            List<CustomField> customFields = Lists.newArrayList(projectFile.getCustomFields().iterator());

            for(Task task : tasks){

                System.out.println("=========");

                task.getChildTasks().size();

                System.out.println("Program Name: ");
                System.out.println("Project Name: ");
                System.out.println("Task Name: " + task.getName());
                System.out.println("Start Date: " + task.getStart());
                System.out.println("Finish Date: " + task.getFinish());
                System.out.println("Percentage Complete: " + task.getPercentageComplete());
                System.out.println("Milestones: " + task.getMilestone());

                for(ResourceAssignment resourceAssignment:  task.getResourceAssignments()){
                    Resource tempResource = resourceAssignment.getResource();
                    if(tempResource != null){
                        System.out.println("Resource Name: " + tempResource.getName());
                    }
                }

                for(CustomField customField : customFields) {
                    if(String.valueOf(task.getFieldByAlias(customField.getAlias())).equals("D")){
                        System.out.println("Is Deliverable");

                    }
                }

                System.out.println("Notes: " + task.getNotes());
            }



        }catch (MPXJException mpxjE){
            mpxjE.printStackTrace();
        }
    }

    private List<TempTask> depthFirstSearch(Task task){
        return new ArrayList<>();
    }
}
