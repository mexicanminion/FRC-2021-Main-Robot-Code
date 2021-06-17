// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class Hopper extends SubsystemBase {
  /** Creates a new Hopper. */

  TalonSRX hopper;
  VictorSPX wacker;
  private boolean toggle = false;

  private void hopperInit(){

    hopper = new TalonSRX(Constants.CANHopper);
    wacker = new VictorSPX(Constants.CANWacker);

  }

  public Hopper() {

    hopperInit();

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void spin(double speed){

    hopper.set(ControlMode.PercentOutput, speed);

  }

  public void wackerSpinOn(){
    wacker.set(ControlMode.PercentOutput, -.5);
  }

  public void wackerSpinOff(){
    wacker.set(ControlMode.PercentOutput, 0);
  }

  public void manuelSpin(){
    if(!(Robot.shooter.getShootSpeed() > 0)){
    
      if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonB) && Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonTriggerRight)){
        spin(-.2);
        SmartDashboard.putString("Hopper", "ccw spin");
      }else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonB)){
        spin(.2);
        SmartDashboard.putString("Hopper", "cw spin");
      }else{
        spin(0);
        SmartDashboard.putString("Hopper", "no spin");

      }
    } 
  }

  public void wackerToggle(){
    if(Robot.oi.getPOVOp() == 180){
      wackerSpinOn();
      
    }else {
      wackerSpinOff();
      
    }
  }

}
