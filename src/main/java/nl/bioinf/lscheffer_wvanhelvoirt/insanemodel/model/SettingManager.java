/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.List;

/**
 *
 * @author lonneke
 */
public abstract class SettingManager {
    /** The list to add error messages to. */
    protected final List<String> errorMessages;

    public SettingManager(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public abstract void addArguments(final List<String> arguments);
    
}
