// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import static edu.wpi.first.wpilibj.DoubleSolenoid.Value.*;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class Pneumatics extends SubsystemBase {
  /** Creates a new Pneumatics. */
  private static Solenoid solenoidIntake;
  private static DoubleSolenoid solenoidLift;
  private static Compressor compressor;
  private Timer timer;

  private double matchTime;

  public void initPneumatics(){
    solenoidIntake = new Solenoid(Constants.CANPneumatics,0);
    solenoidLift = new DoubleSolenoid(Constants.CANPneumatics,1,2);

    compressor = new Compressor(Constants.CANPneumatics);
    timer = new Timer(); 

    compressor.setClosedLoopControl(true);
    solenoidIntake.set(false);
    solenoidLift.set(DoubleSolenoid.Value.kOff);
    SmartDashboard.putString("compressor ", "on?");

  }

  
  public Pneumatics() {
    initPneumatics();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void disablePnematics(){
    solenoidIntake.set(false);
    solenoidLift.set(DoubleSolenoid.Value.kOff);
  }

  public void setIntakePiston(boolean state){

    solenoidIntake.set(state);
    if(state == true){
      
      SmartDashboard.putString("intake ", "forward?");

    }else{

      SmartDashboard.putString("intake ", "backward?");

    }

  }

  public void intakeContoller(){

    matchTime = timer.getMatchTime();
    SmartDashboard.putNumber("match time", matchTime);


    if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonX) && Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonTriggerRight)){
      setIntakePiston(false);
    }else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonX)){
      setIntakePiston(true);
    }

    if(matchTime < 30){//
      SmartDashboard.putString("Can Lift?", "yes");
    
      if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonY) && Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonTriggerRight))
      {

        setLiftPiston(true);
        SmartDashboard.putString("lift state", "down");

      }
      else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonY))
      {

        setLiftPiston(false);
        SmartDashboard.putString("lift state", "up");

      }
    }

  }

  public static void setLiftPiston(boolean state){

    if(state == true){
      solenoidLift.set(DoubleSolenoid.Value.kForward);

    }else{
      solenoidLift.set(DoubleSolenoid.Value.kReverse);
    }


  }
}
