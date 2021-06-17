// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class Intake extends SubsystemBase {
  /** Creates a new Intake. */

  TalonSRX intake;

  public void intakeInit(){

    intake = new TalonSRX(Constants.CANIntake);
    intake.setInverted(true);
  
  }

  public Intake() {
    intakeInit();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void ballIn(){
    intake.set(TalonSRXControlMode.PercentOutput, -1);
  }
  
  public void ballOut(){
    intake.set(TalonSRXControlMode.PercentOutput, 1);
  }
  
  public void ballStop(){
    intake.set(TalonSRXControlMode.PercentOutput, 0);
  }

  public void teleIntake(){
    
    if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonA) && Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonTriggerRight)){
      ballOut();
    }else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonA)){
      ballIn();
    }else{
      ballStop();
    }

  }
}
