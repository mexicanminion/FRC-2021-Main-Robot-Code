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

  /**
   * calls once to set up intake class
   */
  public void intakeInit(){

    intake = new TalonSRX(Constants.CANIntake);
    intake.setInverted(true);
  
  }

  /**
   * constructor
   */
  public Intake() {
    intakeInit();
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  /**
   * intakes balls
   */
  public void ballIn(){
    intake.set(TalonSRXControlMode.PercentOutput, -1);
  }
  
  /**
   * out takes balls
   */
  public void ballOut(){
    intake.set(TalonSRXControlMode.PercentOutput, 1);
  }
  
  /**
   * stops intake motor
   */
  public void ballStop(){
    intake.set(TalonSRXControlMode.PercentOutput, 0);
  }

  /**
   * called periodically to update intake
   * used by OP
   * HOLD A - INTAKE
   * HOLD A + RightBumber - OUTTAKE
   */
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
