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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Plan configuration for Bamboo.
 * Learn more on: <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run main to publish plan on Bamboo
     */
    public static void main(final String[] args) throws Exception {

        Properties p = getValue();

        //By default credentials are read from the '.credentials' file.
        BambooServer bambooServer = new BambooServer(p.getProperty("url"));

        Plan plan = new PlanSpec().createPlan();

        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());

        bambooServer.publish(planPermission);
    }

    static Properties getValue(){
        Properties p = new Properties();
        try{
            InputStream is = new FileInputStream("bambooConfig.properties");
            p.load(is);
        }catch (Exception e){
            e.printStackTrace();
        }
        return p;
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Properties p = getValue();
        Permissions permission = new Permissions()
                .userPermissions(p.getProperty("username"), PermissionType.ADMIN, PermissionType.CLONE, PermissionType.EDIT)
                .groupPermissions("bamboo-admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.VIEW)
                .anonymousUserPermissionView();
        return new PlanPermissions(planIdentifier.getProjectKey(), planIdentifier.getPlanKey()).permissions(permission);
    }

    Project project() {
        return new Project()
                .name("Project Name")
                .key("PRJ");
    }

    Plan createPlan() {
        return new Plan(
                project(),
                "Plan Name", "PLANKEY")
                .description("Plan created from (enter repository url of your plan)")
                .stages(
                        new Stage("Stage 1")
                                .jobs(new Job("Build", "RUN")
                                        .tasks(
                                                new ScriptTask().inlineBody("echo Hello world!"))));

    }


}
