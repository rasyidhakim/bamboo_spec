package tutorial;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args)  {

        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer(System.getenv("bamboo_spec_url"));

        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permission = new Permissions()
                .userPermissions(System.getenv("bamboo_spec_admin_username"), PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
                .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.VIEW)
                .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
                .name(System.getenv("bamboo_spec_project_name"))
                .key(System.getenv("bamboo_spec_project_key"));
    }

    Plan createPlan() {
        File task = new File("task.txt");
        String data="";
        try {
            Scanner fScn = new Scanner(task);
            while (fScn.hasNextLine()){
                data = data + fScn.nextLine() + "\n";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(data);
        return new Plan(
                project(),
                System.getenv("bamboo_spec_project_plan_name"), System.getenv("bamboo_spec_project_plan_key"))
                .description(System.getenv("bamboo_spec_project_plan_description"))
                .stages(
                        new Stage("Stage 1")
                                .jobs(new Job("Build", "RUN")
                                        .tasks(
                                                new ScriptTask().inlineBody(data))));

    }


}
