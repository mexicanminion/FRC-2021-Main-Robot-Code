// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import frc.robot.Constants;
import frc.robot.Robot;

import java.lang.Math.*;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.kinematics.*;
import edu.wpi.first.wpilibj.geometry.*;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;;


public class DriveBase extends SubsystemBase {
  /** Creates a new DriveBase. */

  TalonFX rightFront;
  TalonFX rightFollower;
  TalonFX leftFront;
  TalonFX leftFollower;


  private double leftCurrentPercentJoystick = 0.0;
  private double rightCurrentPercentJoystick = 0.0;
  private double leftCurrentPercentAuto = 0.0;
  private double rightCurrentPercentAuto = 0.0;
  private double leftCurrentPercent = 0.0;
  private double rightCurrentPercent = 0.0;
  private int decelLoopCount = 0;
  private boolean autoFlag = false;
  private boolean velFlag = false;
  private boolean speedShift = false;

  private double leftSpeedFinal = 0;
  private double rightSpeedFinal = 0;

  private void driveBaseInit(){

    leftFront = new TalonFX(Constants.CANLeftFrontMaster); 
    leftFollower = new TalonFX(Constants.CANLeftFrontFollower); 
    rightFront = new TalonFX(Constants.CANRightFrontMaster); 
    rightFollower = new TalonFX(Constants.CANRightFrontFollower);

    leftFront.setInverted(false);
    leftFollower.setInverted(false);
    rightFront.setInverted(true);
    rightFollower.setInverted(true);

    leftFront.setNeutralMode(NeutralMode.Coast);
    leftFollower.setNeutralMode(NeutralMode.Coast);
    rightFront.setNeutralMode(NeutralMode.Coast);
    rightFollower.setNeutralMode(NeutralMode.Coast);

    rightFollower.follow(rightFront);
    leftFollower.follow(leftFront);

    leftFront.configFactoryDefault();
    leftFollower.configFactoryDefault();
    rightFront.configFactoryDefault();
    rightFollower.configFactoryDefault();

    rightFront.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0,30);
    leftFront.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0,30);

    leftFront.clearStickyFaults();
    leftFollower.clearStickyFaults();
    rightFront.clearStickyFaults();
    rightFollower.clearStickyFaults();

    // rightFront.setSelectedSensorPosition(0);
    // leftFront.setSelectedSensorPosition(0);


    //leftFront.config_kF(0, Constants.DrivePIDkF, Constants.kTimeoutMs);
		leftFront.config_kP(0, Constants.DrivePIDkP, Constants.kTimeoutMs);
		leftFront.config_kI(0, Constants.DrivePIDkI, Constants.kTimeoutMs);
    //leftFront.config_kD(0, Constants.DrivePIDkD, Constants.kTimeoutMs);
    //^^ commmented and broke code  

    //rightFront.config_kF(0, Constants.DrivePIDkF, Constants.kTimeoutMs);
		rightFront.config_kP(0, Constants.DrivePIDkP, Constants.kTimeoutMs);
		rightFront.config_kI(0, Constants.DrivePIDkI, Constants.kTimeoutMs);
		//rightFront.config_kD(0, Constants.DrivePIDkD, Constants.kTimeoutMs);

    rightFront.set(TalonFXControlMode.Velocity, 0);
    leftFront.set(TalonFXControlMode.Velocity, 0);
  }


  public DriveBase(){

    driveBaseInit();
    zeroEncoders();
    fullStop();

  }

  public void parkingBrake(boolean isOn){

    if(isOn == true){
      leftFront.setNeutralMode(NeutralMode.Brake);
      leftFollower.setNeutralMode(NeutralMode.Brake);
      rightFront.setNeutralMode(NeutralMode.Brake);
      rightFollower.setNeutralMode(NeutralMode.Brake);  
    }else{
      leftFront.setNeutralMode(NeutralMode.Coast);
      leftFollower.setNeutralMode(NeutralMode.Coast);
      rightFront.setNeutralMode(NeutralMode.Coast);
      rightFollower.setNeutralMode(NeutralMode.Coast);
    }
  
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void runToPos(double pos, double speed){
    //-1 to 1 speed, converts to RPM
    double leftTick = 0;
    double rightTick = 0;
    boolean done = false;

    pos = pos/Constants.WheelInchPerTicks;
    SmartDashboard.putNumber("wanted pos", pos);

    double avg = 0;

    //System.out.println("Ran once");

    while(done == false){

      //System.out.println("Running");

      leftTick = leftFront.getSelectedSensorPosition();
      rightTick = rightFront.getSelectedSensorPosition();
      SmartDashboard.putNumber("right tick", rightTick);
      SmartDashboard.putNumber("left tick", leftTick);

      avg = (leftTick + rightTick) / 2;

      if(Math.abs(pos-avg) <= 50){
        //System.out.println("Case1");
        done = true;
      }else if(Math.abs(avg)>Math.abs(pos)){
        // System.out.println(avg);
        // System.out.println(pos);
        // System.out.println("Case2");

        done = true;
      }

      drive(speed, speed);

    } 

    fullStop();
    zeroEncoders();

    /*double driveTicks = driveInches * Constants.WheelInchPerTicks;

    rightFront.set(TalonFXControlMode.Position, driveTicks);
    leftFront.set(TalonFXControlMode.Position, driveTicks);
    
    old concept code DONT USE
    
    */

  }

  /*public void drivePower(double leftSpeed, double rightSpeed){

    if(leftSpeed > leftSpeedFinal){
      leftSpeedFinal = leftSpeedFinal + Constants.MaxAccel;
    }else if(leftSpeed < leftSpeedFinal){
      leftSpeedFinal = leftSpeedFinal - Constants.MaxDecel;
    }
    
    if(rightSpeed > rightSpeedFinal){
      rightSpeedFinal = rightSpeedFinal + Constants.MaxAccel;
    }else if(rightSpeed < rightSpeedFinal){
      rightSpeedFinal = rightSpeedFinal - Constants.MaxDecel;
    }

    rightFront.set(TalonFXControlMode.PercentOutput, rightSpeedFinal);
    leftFront.set(TalonFXControlMode.PercentOutput, leftSpeedFinal);

  }*/
  

  public void drive(double leftSpeed, double rightSpeed) {

    SmartDashboard.putNumber("curr vel left", leftSpeedFinal);
    SmartDashboard.putNumber("curr vel right", rightSpeedFinal);

    SmartDashboard.putNumber("Target vel left", leftSpeed);
    SmartDashboard.putNumber("Target vel right", rightSpeed);

    //final is current
    //speed is joy
    leftSpeedFinal= setSpeed(leftSpeed, leftSpeedFinal);
    rightSpeedFinal = setSpeed(rightSpeed, rightSpeedFinal);
    

    /*if((leftSpeed> 0) && (leftSpeedFinal < 0) || (leftSpeed < 0) && (leftSpeedFinal > 0)){
      if(leftSpeed > leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal + Constants.MaxDecel;//accel before decel change
      }else if(leftSpeed < leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal - Constants.MaxDecel;
      }

    }else if(leftSpeed > 0){

      if(leftSpeed > leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal + Constants.MaxAccel;
      }else if(leftSpeed < leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal - Constants.MaxDecel;
      }

    }else{
      if(leftSpeed > leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal + Constants.MaxDecel;
      }else if(leftSpeed < leftSpeedFinal){
        leftSpeedFinal = leftSpeedFinal - Constants.MaxAccel;
      }  
    }

    if((rightSpeed> 0) && (rightSpeedFinal < 0) || (rightSpeed < 0) && (rightSpeedFinal > 0)){

      if(rightSpeed > rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal + Constants.MaxDecel;//accel before Decel change
      }else if(rightSpeed < rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal - Constants.MaxDecel;
      }

    }else if(rightSpeed > 0){

      if(rightSpeed > rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal + Constants.MaxAccel;
      }else if(rightSpeed < rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal - Constants.MaxDecel;
      }

    }else{ 

      if(rightSpeed > rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal + Constants.MaxDecel;
      }else if(rightSpeed < rightSpeedFinal){
        rightSpeedFinal = rightSpeedFinal - Constants.MaxAccel;
      }

    }*/
    
    if(velFlag == true){
      rightFront.set(TalonFXControlMode.Velocity, rightSpeedFinal* Constants.FalconVelRatio* Constants.MaxRPM);
      leftFront.set(TalonFXControlMode.Velocity, leftSpeedFinal* Constants.FalconVelRatio* Constants.MaxRPM);
    }else{
      rightFront.set(TalonFXControlMode.PercentOutput, rightSpeedFinal);
      leftFront.set(TalonFXControlMode.PercentOutput, leftSpeedFinal);
      
    }

  }

  /*public void deceleration(double currVelLeft, double targVelLeft, double currVelRight, double targVelRight){ //Not decelerating... Loop time too fast???
    double deltaVelRight = targVelRight - currVelRight;
    double deltaVelLeft = targVelLeft - currVelLeft;
    if(decelLoopCount == 10){
      if(Math.abs(deltaVelLeft) < Constants.MaxDecel){
        leftSpeedFinal = targVelLeft;
        SmartDashboard.putString("Decelerating left?", "No");
      }
      else if(targVelLeft > currVelLeft){
        leftSpeedFinal = currVelLeft + Math.sqrt(Constants.MaxDecel);
        SmartDashboard.putString("Decelerating left?", "Yes0");
      }
      else if(targVelLeft < currVelLeft){
        leftSpeedFinal = currVelLeft - Math.sqrt(Constants.MaxDecel);
        SmartDashboard.putString("Decelerating left?", "Yes1");
      }
      if(Math.abs(deltaVelRight) < Constants.MaxDecel){
        rightSpeedFinal = targVelRight;
        SmartDashboard.putString("Decelerating right?", "No");
      }
      else if(targVelRight > currVelRight){
        rightSpeedFinal = currVelRight + Math.sqrt(Constants.MaxDecel);
        SmartDashboard.putString("Decelerating right?", "Yes0");
      }
      else if(targVelRight < currVelRight){
        rightSpeedFinal = currVelRight - Math.sqrt(Constants.MaxDecel);
        SmartDashboard.putString("Decelerating right?", "Yes1");
      }
      decelLoopCount = 0;
    }
    decelLoopCount ++;
    
  }*/

  public void driveWithJoySticks(double left, double right){

    if(speedShift == true){
      drive(left/2, right/2);
    }else{
      drive(left/2.5, right/2.5);
    }

  }

  public void gyroRotate(double angleYaw){
    boolean isDone = false;
    double angle = angleYaw;
    double currAngle = Robot.imu.getAngleZ();
    double angleDiff = angle - currAngle;
    double currAngleDiff;
    double speed = 0;
    double localMod = Constants.AutoRotatekP;
    while(!isDone){
        currAngle = Robot.imu.getAngleZ();
        currAngleDiff = angle - currAngle;
        if(Math.abs(currAngleDiff) >= 90)speed = localMod;
        else if(Math.abs(currAngleDiff) >= 30)speed = Math.abs(currAngleDiff/angleDiff) * localMod * Constants.AutoSlowDown + Constants.AutoRotateConstant;
        if(currAngleDiff < -Constants.AutoRotateError){
            Robot.driveBase.drive(speed, -speed);
        }else if(currAngleDiff > Constants.AutoRotateError){
            Robot.driveBase.drive(-speed, speed);
        }else{
            isDone = true;
            Robot.driveBase.drive(0, 0);
        }
        //Robot.driveBase.updateDriveTrain();
    }
    Robot.driveBase.fullStop();
  }

  public void fullStop(){

    leftSpeedFinal= 0;
    rightSpeedFinal = 0;

    leftFront.set(TalonFXControlMode.PercentOutput,0);
    rightFront.set(TalonFXControlMode.PercentOutput,0);

    
  }

  public void setVelFlag(boolean setFlag){
    velFlag = setFlag;
  }

  public void zeroEncoders() {
    leftFront.setSelectedSensorPosition(0);
    rightFront.setSelectedSensorPosition(0);
  }

  public void speedShift(){
    if(Robot.oi.getControllerButtonState(Constants.XBoxButtonA)){
      speedShift = true;
    }else{
      speedShift = false;
    }
  }

  public double setSpeed(double speed, double fin){
    if((speed > 0) && (fin < 0) || (speed  < 0) && (fin > 0)){

      if(speed > fin){
        fin  = fin + Constants.MaxDecel;//accel before Decel change
      }else if(speed < fin){
        fin = fin - Constants.MaxDecel;
      }

    }else if(speed == 0){
      if(speed > fin){
        fin  = fin + Constants.MaxDecel;//accel before Decel change
      }else if(speed < fin){
        fin = fin - Constants.MaxDecel;
      }

    }else if(speed > 0){

      if(speed > fin){
        fin = fin + Constants.MaxAccel;
      }else if(speed < fin){
        fin = fin - Constants.MaxDecel;
      }

    }else{ 

      if(speed > fin){
        fin = fin + Constants.MaxDecel;
      }else if(speed < fin){
        fin = fin - Constants.MaxAccel;
      }

    }
    return fin;
  }

  public void updateDriveBase(){
    //speedShift();
    driveWithJoySticks(Robot.oi.getControllerStickLeft(), Robot.oi.getControllerStickRight());
  }

}
