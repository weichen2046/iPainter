package com.training.ipainter.model;

class DrawableAlreadyExistException extends RuntimeException {
    public DrawableAlreadyExistException(String msg) {
        super(msg);
    }
}
