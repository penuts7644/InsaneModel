/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author lonneke
 */
public class Martinize extends SettingManager {
    private final String secondaryStructure;
    private final boolean collagen;
    private final boolean neutralTermini;
    private final boolean chargeChainBreaks;
    private final double disulphideDistance;
    private final boolean link;
    private final boolean mergeChains;
    private final String positionRestraints;
    private final int forceConstant;
    private final boolean dihedrals;
    private final boolean separateTopologies;
    private final String forceField;
            
    
    private static final Set<String> VALID_FORCEFIELDS = new HashSet<String>(Arrays.asList(
        new String[] {"elnedyn","martini22p","elnedyn22p","martini21", "elnedyn22", "martini21p", "martini22"}
    ));
    
    private static final Set<String> VALID_POSITION_RESTRAINTS = new HashSet<String>(Arrays.asList(
        new String[] {"None","All","Backbone"}
    ));

    public Martinize(List<String> errorMessages,
                     final String secondaryStructure,
                     final boolean collagen,
                     final boolean neutralTermini,
                     final boolean chargeChainBreaks,
                     final double disulphide,
                     final boolean link,
                     final boolean mergeChains,
                     final String positionRestraints,
                     final int forceConstant,
                     final boolean dihedrals,
                     final boolean separateTopologies,
                     final String forceField) {
        
        super(errorMessages);
        this.secondaryStructure = secondaryStructure;
        this.collagen = collagen;
        this.neutralTermini = neutralTermini;
        this.chargeChainBreaks = chargeChainBreaks;
        this.disulphideDistance = disulphide;
        this.link = link;
        this.mergeChains = mergeChains;
        if (Martinize.VALID_POSITION_RESTRAINTS.contains(positionRestraints)){
            this.positionRestraints = positionRestraints;
        } else{
            this.positionRestraints = "None";
        }
        this.forceConstant = Math.abs(forceConstant);
        this.dihedrals = dihedrals;
        this.separateTopologies = separateTopologies;
        if (Martinize.VALID_FORCEFIELDS.contains(forceField)){
            this.forceField = forceField;
        } else {
            this.forceField = "martini22";
        }
        
    }

    @Override
    public void addArguments(List<String> arguments) {
        // Always add the force field
        arguments.add("-ff");
        arguments.add(this.forceField);
        if (!this.secondaryStructure.equals("")){
            arguments.add("-ss");
            arguments.add(this.secondaryStructure);
        }
        if (this.collagen){
            arguments.add("-collagen");
        }
        if (this.neutralTermini){
            arguments.add("-nt");
        }
        if (this.chargeChainBreaks){
            arguments.add("-cb");
        }
        if (this.disulphideDistance > 0){
            arguments.add("-cys");
            arguments.add(Double.toString(this.disulphideDistance));
        }
        if (this.link){
            arguments.add("-link");
        }
        if (this.mergeChains) {
            arguments.add("-merge");
        }
        arguments.add("-p");
        arguments.add(this.positionRestraints);
        if (this.forceConstant > 0){
            arguments.add("-pf");
            arguments.add(String.valueOf(this.forceConstant));
        }
        if (this.dihedrals){
            arguments.add("-ed");
        }
        if (this.separateTopologies){
            arguments.add("-sep");
        }
    }
    
}
