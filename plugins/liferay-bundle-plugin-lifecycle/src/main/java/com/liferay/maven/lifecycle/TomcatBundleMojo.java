package com.liferay.maven.lifecycle;

import java.io.File;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Profile;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.FileUtils;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;

@Mojo(name = "package", defaultPhase = LifecyclePhase.PACKAGE)
public class TomcatBundleMojo extends AbstractMojo {

	@Component
	protected ArchiverManager archiverManager;

	@Component
	protected ArtifactFactory artifactFactory;

	@Component
	protected ArtifactHandler artifactHandler;

	@Component
	protected ArtifactResolver artifactResolver;

	@Component
	private MavenProject project;

	@Parameter(required = true, readonly = true, defaultValue = "${localRepository}")
	protected ArtifactRepository localRepository;

	@Parameter(required = true, readonly = true, defaultValue = "${project.remoteArtifactRepositories}")
	protected List<ArtifactRepository> remoteRepositories;

	@Parameter(defaultValue = "7.0.40", alias = "tomcat.version", required = true)
	private String tomcatVersion;

	@Parameter(defaultValue = "true", alias = "tomcat.cleanup.webapps", required = true)
	private boolean tomcatCleanupWebapps;

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	protected Artifact resolveTomcatArtifact() {
		ArtifactResolutionRequest artifactResolutionRequest = new ArtifactResolutionRequest();

		Artifact artifact = artifactFactory.createArtifact("org.apache.tomcat",
				"tomcat", tomcatVersion, Artifact.SCOPE_COMPILE, "tar.gz");

		artifactResolutionRequest.setArtifact(artifact);
		artifactResolutionRequest.setLocalRepository(localRepository);
		artifactResolutionRequest.setRemoteRepositories(remoteRepositories);

		ArtifactResolutionResult artifactResolutionResult = artifactResolver
				.resolve(artifactResolutionRequest);

		return artifact;
	}

	public void execute() throws MojoExecutionException {

		Artifact tomcatArtifact = resolveTomcatArtifact();

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		if (tomcatArtifact != null) {
			extractTomcat(tomcatArtifact);
			copyTomcatFilesFromProfile();
		}
	}

	protected void extractTomcat(Artifact tomcatArtifact) {
		File file = tomcatArtifact.getFile();
		try {
			UnArchiver unArchiver = archiverManager.getUnArchiver(file);

			unArchiver.setSourceFile(file);
			unArchiver.setDestDirectory(outputDirectory);

			unArchiver.extract();

			File oldTomcatHome = new File(outputDirectory, "apache-tomcat-"
				+ tomcatArtifact.getVersion());

			File newTomcatHome = new File(outputDirectory, "tomcat-"
				+ tomcatArtifact.getVersion());

			FileUtils.rename(oldTomcatHome, newTomcatHome);

			if (tomcatCleanupWebapps) {
				File webappsDirectory = new File(newTomcatHome, "webapps");

				FileUtils.cleanDirectory(webappsDirectory);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	protected void copyTomcatFilesFromProfile() {
		File profilesDirectory = new File(project.getBasedir(),
				"src/main/profiles/");
		List<Profile> profiles = project.getActiveProfiles();

		for (Profile profile : profiles) {
			File profileDirectory = new File(profilesDirectory, profile.getId());
			if (profileDirectory.exists()) {
				File tomcatSourceDirectory = new File(profileDirectory,
						"tomcat");
				if (tomcatSourceDirectory.exists()) {
					try {
						File tomcatTargetDirectory = new File(outputDirectory,
								"tomcat-" + tomcatVersion);
						FileUtils.copyDirectoryStructure(tomcatSourceDirectory,
								tomcatTargetDirectory);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
