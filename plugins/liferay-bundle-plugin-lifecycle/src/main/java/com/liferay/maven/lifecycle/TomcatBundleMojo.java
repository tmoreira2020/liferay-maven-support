package com.liferay.maven.lifecycle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
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
	protected ArtifactHandler artifactHandler;

	@Component
	protected ArtifactResolver artifactResolver;

	@Component
	private MavenProject project;

	@Parameter(required = true, readonly = true, defaultValue = "${localRepository}")
	protected ArtifactRepository localRepository;

	@Parameter(required = true, readonly = true, defaultValue = "${project.remoteArtifactRepositories}")
	protected List<ArtifactRepository> remoteRepositories;

	@Parameter(alias = "liferay.version", required = true)
	private String liferayVersion;

	@Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
	private File outputDirectory;

	protected Artifact resolveliferayPortalArtifact() {
		ArtifactResolutionRequest artifactResolutionRequest = new ArtifactResolutionRequest();

		Artifact artifact = new DefaultArtifact("com.liferay.portal",
			"liferay-portal", liferayVersion, Artifact.SCOPE_COMPILE, "zip", "tomcat", artifactHandler);

		artifactResolutionRequest.setArtifact(artifact);
		artifactResolutionRequest.setLocalRepository(localRepository);
		artifactResolutionRequest.setRemoteRepositories(remoteRepositories);

		artifactResolver.resolve(artifactResolutionRequest);

		return artifact;
	}

	public void execute() throws MojoExecutionException {

		Artifact liferayPortalArtifact = resolveliferayPortalArtifact();

		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}

		try {
			if (liferayPortalArtifact != null) {
				extractTomcat(liferayPortalArtifact);
			}
		} catch (Exception e) {
			getLog().error(e);
		}
	}

	protected void extractTomcat(Artifact liferayPortalArtifact) throws Exception {
		getLog().info("Unpacking " + liferayPortalArtifact.getArtifactId() + "-" + liferayPortalArtifact.getVersion());

		File file = liferayPortalArtifact.getFile();

		UnArchiver unArchiver = archiverManager.getUnArchiver(file);

		unArchiver.setSourceFile(file);
		unArchiver.setDestDirectory(outputDirectory);

		unArchiver.extract();

	}

	protected void copyTomcatFilesFromProfile() throws IOException {
		File profilesDirectory = new File(project.getBasedir(),
				"src/main/profiles/");
		List<Profile> profiles = project.getActiveProfiles();

		for (Profile profile : profiles) {
			File profileDirectory = new File(profilesDirectory, profile.getId());
			if (profileDirectory.exists()) {
				File tomcatSourceDirectory = new File(profileDirectory,
						"tomcat");
				if (tomcatSourceDirectory.exists()) {
					File tomcatTargetDirectory = new File(outputDirectory,
							"liferay-portal-6.1.1-ce-ga2/tomcat-7.0.27");
					FileUtils.copyDirectoryStructure(tomcatSourceDirectory,
							tomcatTargetDirectory);
				}
			}
		}
	}

}
