package dev.infochem.cmakegradleplugin;

import dev.infochem.cmakegradleplugin.util.BuildType;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import dev.infochem.cmakegradleplugin.util.NativePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class CMakePlugin implements Plugin<Project> {
    public static final String gradleTasksGroup = "CMake";
    public static final String CONFIGURE_CMAKE_TASK_NAME = "ConfigureCMake";
    public static final String BUILD_CMAKE_TASK_NAME = "BuildCMake";
    public static final String DSL_EXTENSION_NAME = "cmake";

    public static final String DEFAULT_BUILD_DIRECTORY_NAME = "cmake";
    public static final String DEFAULT_SOURCE_DIRECTORY = "src/main/cpp";

    private final Logger logger = LoggerFactory.getLogger(CMakePlugin.class);

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(JavaBasePlugin.class);
        CMakeExtension cmakeExtension = project.getExtensions().create(DSL_EXTENSION_NAME, CMakeExtension.class);

        TaskContainer tasks = project.getTasks();

        final TaskProvider<CMakeConfigurationTask> configureCMake = tasks.register(CONFIGURE_CMAKE_TASK_NAME, CMakeConfigurationTask.class);
        final TaskProvider<CMakeBuildTask> buildCMake = tasks.register(BUILD_CMAKE_TASK_NAME, CMakeBuildTask.class);

        tasks.named("assemble").configure(task -> task.dependsOn(buildCMake));
        buildCMake.configure(task -> task.dependsOn(configureCMake));
        project.afterEvaluate(p -> {
            setDefaultValue(cmakeExtension.getCMakeExecutable(), NativePlatform.getCMakeExecutable().getAbsolutePath());
            setDefaultValue(cmakeExtension.getBuildDirectory(), project.getLayout().getBuildDirectory().dir(DEFAULT_BUILD_DIRECTORY_NAME).get());
            setDefaultValue(cmakeExtension.getSourceDirectory(), project.getLayout().getProjectDirectory().dir(DEFAULT_SOURCE_DIRECTORY));
            setDefaultValue(cmakeExtension.getBuildType(), BuildType.DEBUG);
        });

    }

    private <T extends Property<U>, U> void setDefaultValue(T property, U defaultValue) {
        if (!property.isPresent()) {
            logger.debug("Set default value(\"{}\") to property {}", defaultValue, property);
            property.set(defaultValue);
        }
    }
}