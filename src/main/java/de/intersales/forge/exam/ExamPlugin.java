package de.intersales.forge.exam;

import java.io.StringWriter;
import java.util.Properties;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.SetupCommand;

import de.intersales.forge.exam.facets.ExamFacet;

/**
 *
 */
@RequiresFacet(ExamFacet.class)
@Alias("exam")
public class ExamPlugin implements Plugin {
	@Inject
	private ShellPrompt prompt;

	@Inject
	private Event<InstallFacets> event;

	@Inject
	private Project project;

	static {
		Properties properties = new Properties();
		properties.setProperty("resource.loader", "class");
		properties
				.setProperty("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		Velocity.init(properties);
	}

	@SetupCommand
	public void setup(PipeOut out) {
		
		if (!project.hasFacet(ExamFacet.class)) {
			event.fire(new InstallFacets(ExamFacet.class));
		} else {
			out.println("project is already setup as pax exam project");
			return;
		}
	}
	
	@Command("new-test")
	public void newTest(@Option(name="name") String className, PipeOut out) {
		
		if (className == null || className.equals("")) {
			className = prompt.prompt("name of the route builder class");
		}
		
		String javaPackage = prompt.prompt("package", project.getFacet(MetadataFacet.class).getTopLevelPackage());
		String karafContainerVersion = prompt.prompt("which karaf container version do you like to test with", "2.3.3");
		
		VelocityContext context = new VelocityContext();
		context.put("package", javaPackage);
		context.put("className", className);
		context.put("karafVersion", karafContainerVersion);
		
		String classContent = getClassContent(context);
		
		JavaClass routeBuilderClass = JavaParser.parse(JavaClass.class, classContent);
		
		try {
			project.getFacet(JavaSourceFacet.class).saveTestJavaSource(routeBuilderClass);
		} catch (Exception e) {
			out.println("error occured while creating source file");
		}
	}

	private String getClassContent(VelocityContext context) {
		StringWriter writer = new StringWriter();
		Velocity.mergeTemplate("templates/KarafTest.java.vtl", "UTF-8", context, writer);
		return writer.toString();
	}
}
