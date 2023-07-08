package com.edu.hogwartsartifactonline.wizard;

import com.edu.hogwartsartifactonline.artifact.Artifact;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Wizard implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "owner")
    @JsonIgnoreProperties("wizard")
    private List<Artifact> artifacts = new ArrayList<>();

    public Wizard() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Artifact> getArtifacts() {
        return artifacts;
    }

    public void setArtifacts(List<Artifact> artifacts) {
        this.artifacts = artifacts;
    }

    public void addArtifact(Artifact artifact){
        artifact.setOwner(this);
        this.artifacts.add(artifact);
    }

    public Integer numberOfArtifacts(){
        return artifacts.size();
    }

    public void removeAllArtifacts(){
        artifacts.forEach(artifact -> artifact.setOwner(null));
        artifacts.clear();
    }
}
