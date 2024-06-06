package ar.edu.utn.frba.tacs.exception;

import lombok.Getter;

@Getter
public class DuplicatedEmailException extends IllegalArgumentException{

    private final String duplicatedEmail;

    public DuplicatedEmailException(String duplicatedEmail){
        super(String.format("Error! Email %s already in use", duplicatedEmail));
        this.duplicatedEmail = duplicatedEmail;
    }

}
