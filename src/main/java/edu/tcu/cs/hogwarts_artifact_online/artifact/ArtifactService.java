package edu.tcu.cs.hogwarts_artifact_online.artifact;

import edu.tcu.cs.hogwarts_artifact_online.artifact.util.IdWorker;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

    private final ArtifactRepository artifactRepository;

    private final IdWorker idWorker;

    public ArtifactService(ArtifactRepository artifactRepository,
                           IdWorker idWorker) {
        this.artifactRepository = artifactRepository;
        this.idWorker = idWorker;
    }

    public Artifact findById(String artifactId) {
        return this.artifactRepository
                .findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public List<Artifact> findAll() {
        return this.artifactRepository.findAll();
    }

    public Artifact save(Artifact newArtifact) {
        newArtifact.setId(String.valueOf(idWorker.nextId()));
        return this.artifactRepository.save(newArtifact);
    }

    public Artifact update(String artifactId, Artifact newerArtifactData) {
        /// Old Code that not using error handling.
        /// Artifact updatedArtifact = this.artifactRepository.findById(artifactId).get();
        /// updatedArtifact.setName(updatingArtifactData.getName());
        /// updatedArtifact.setDescription(updatingArtifactData.getDescription());
        /// updatedArtifact.setImageUrl(updatingArtifactData.getImageUrl());
        ///
        /// return this.artifactRepository.save(updatedArtifact);
        return this.artifactRepository.findById(artifactId).map(updatedArtifact -> {
                    updatedArtifact.setName(newerArtifactData.getName());
                    updatedArtifact.setDescription(newerArtifactData.getDescription());
                    updatedArtifact.setImageUrl(newerArtifactData.getImageUrl());
                    return this.artifactRepository.save(updatedArtifact);
                })
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
    }

    public void delete(String artifactId) {
        this.artifactRepository.findById(artifactId)
                .orElseThrow(() -> new ArtifactNotFoundException(artifactId));
        this.artifactRepository.deleteById(artifactId);
    }
}
