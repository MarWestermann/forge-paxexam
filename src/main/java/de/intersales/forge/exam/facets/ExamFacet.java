package de.intersales.forge.exam.facets;

import javax.inject.Inject;

import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.BaseFacet;

public class ExamFacet extends BaseFacet {

	private static final Dependency KARAF_FEATURES = cD(
			"org.apache.karaf.features", "standard", "features", "xml");
	private static final Dependency PAX_EXAM = cD("org.ops4j.pax.exam",
			"pax-exam");
	private static final Dependency PAX_EXAM_CONTAINER_KARAF = cD(
			"org.ops4j.pax.exam", "pax-exam-container-karaf");
	private static final Dependency PAX_EXAM_JUNIT4 = cD("org.ops4j.pax.exam",
			"pax-exam-junit4");
	private static final Dependency PAX_URL_AETHER = cD("org.ops4j.pax.url",
			"pax-url-aether");
	private static final Dependency JAVAX_INJECT = cD("javax.inject",
			"javax.inject");
	private static final Dependency JUNIT4 = cD("junit", "junit");

	private static final Dependency SLF4J_API_DEPENDENCY = cD("org.slf4j",
			"slf4j-api");

	@Inject
	private DependencyInstaller installer;

	@Override
	public boolean install() {

		createMavenDependencies();
		addMavenBuildPlugins();
		return true;
	}

	private void addMavenBuildPlugins() {
		MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);

		DependencyBuilder mvnCompilerPluginDependency = cDB(
				"org.apache.maven.plugins", "maven-compiler-plugin");
		DependencyBuilder mvnDependsPluginDependency = cDB(
				"org.apache.servicemix.tooling", "depends-maven-plugin");

		MavenPluginBuilder mavenCompilerPlugin = buildMavenCompilerPlugin(mvnCompilerPluginDependency);
		MavenPluginBuilder mavenDependsPlugin = buildMavenDependsPlugin(mvnDependsPluginDependency);
		
		pluginFacet.addPlugin(mavenCompilerPlugin);
		pluginFacet.addPlugin(mavenDependsPlugin);

	}

	private MavenPluginBuilder buildMavenDependsPlugin(
			DependencyBuilder mvnDependsPluginDependency) {
		MavenPluginBuilder mavenDependsPlugin = MavenPluginBuilder.create()
				.setDependency(mvnDependsPluginDependency);
		mavenDependsPlugin.addExecution(
				ExecutionBuilder.create()
                .setId("generate-depends-file")
                .addGoal("generate-depends-file")
				);
		return mavenDependsPlugin;
	}

	private MavenPluginBuilder buildMavenCompilerPlugin(
			DependencyBuilder mvnCompilerPluginDependency) {
		
		MavenPluginBuilder mavenCompilerPlugin = MavenPluginBuilder.create()
				.setDependency(mvnCompilerPluginDependency);

		ConfigurationBuilder configurationBuilder = mavenCompilerPlugin
				.createConfiguration();
		configurationBuilder.createConfigurationElement("source")
				.setText("1.6");
		configurationBuilder.createConfigurationElement("target")
				.setText("1.6");
		return mavenCompilerPlugin;
	}

	private void createMavenDependencies() {
		installer.install(project, KARAF_FEATURES);
		installer.install(project, JUNIT4);
		installer.install(project, SLF4J_API_DEPENDENCY);
		
		installer.install(project, JAVAX_INJECT);
		
		installer.install(project, PAX_EXAM);
		installer.install(project, PAX_EXAM_CONTAINER_KARAF);
		installer.install(project, PAX_EXAM_JUNIT4);
		installer.install(project, PAX_URL_AETHER);

	}

	@Override
	public boolean isInstalled() {
		return (installer.isInstalled(getProject(), PAX_EXAM));
	}

	private static Dependency cD(String groupId, String artifactId) {
		return DependencyBuilder.create().setGroupId(groupId)
				.setArtifactId(artifactId);
	}

	private static Dependency cD(String groupId, String artifactId,
			String classifier, String type) {
		return DependencyBuilder.create().setGroupId(groupId)
				.setArtifactId(artifactId).setClassifier(classifier)
				.setPackagingType(type);
	}

	private static DependencyBuilder cDB(String groupId, String artifactId) {
		return DependencyBuilder.create().setArtifactId(artifactId)
				.setGroupId(groupId);
	}

}
