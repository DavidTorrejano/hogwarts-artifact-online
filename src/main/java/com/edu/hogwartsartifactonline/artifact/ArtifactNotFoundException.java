package com.edu.hogwartsartifactonline.artifact;

public class ArtifactNotFoundException extends RuntimeException{

    public ArtifactNotFoundException (String id){
        super("Could not find artifact with Id: " + id + ", we are sorry :/");
    }
}
