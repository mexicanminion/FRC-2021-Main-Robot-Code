// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.nio.channels.spi.SelectorProvider;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Robot;

public class Shooter extends SubsystemBase {

  private TalonFX Shooter;
  private TalonSRX actuator;
  private Encoder safety;
  private Timer time;
  private boolean timeThrough = true;
  private boolean toggle = false;


  private double shooterSpeed = .95;


  private void shooterInit(){

    Shooter = new TalonFX(Constants.CANShooter);
    actuator = new TalonSRX(5);
    safety = new Encoder(0, 1);
    time = new Timer();

    safety.reset();
    //actuator.configSelectedFeedbackSensor(safety);

    Shooter.setInverted(true);
    Shooter.configFactoryDefault();
    Shooter.clearStickyFaults();
    Shooter.setNeutralMode(NeutralMode.Coast);
    Shooter.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,0,30);
    Shooter.config_kP(0, Constants.shooterkP, Constants.kTimeoutMs);
		Shooter.config_kI(0, Constants.shooterkI, Constants.kTimeoutMs);

  }

  /** Creates a new Shooter. */
  public Shooter() {

    shooterInit();

  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  public void manuelShoot(){
    double targetVel = Constants.maxShooterRPM * Constants.FalconVelRatio;

    if(Robot.oi.getControllerButtonState(Constants.XBoxButtonB)){
      //Shooter.set(TalonFXControlMode.PercentOutput, .9);
      Shooter.set(TalonFXControlMode.Velocity, targetVel);
      SmartDashboard.putNumber("shooter vel", Shooter.getSelectedSensorVelocity());
      SmartDashboard.putNumber("target shoot vel", targetVel);
      SmartDashboard.putString("manuel shooter", "is on");
    }else{
      Shooter.set(TalonFXControlMode.PercentOutput, 0);
      SmartDashboard.putString("manuel shooter", "is off");
    }
  }

  /*public void actuate(){
    if(Robot.oi.getControllerButtonState(Constants.XBoxButtonY)){
      actuator.set(TalonSRXControlMode.PercentOutput, .3);
    }
    if(Robot.oi.getControllerButtonState(Constants.XBoxButtonX)){
      actuator.set(TalonSRXControlMode.PercentOutput, -.3);
    }
  }*/

  public void roboShoot(boolean isOn){


    if(isOn == true){
      Shooter.set(TalonFXControlMode.Velocity, Constants.maxShooterRPM * Constants.FalconVelRatio);
      SmartDashboard.putString("shooter", "is on");
    }else{
      Shooter.set(TalonFXControlMode.Velocity, 0);
      SmartDashboard.putString("shooter", "is off");
    }

    //int timeoutLoop = wait * 20;
    //setPoint = Constants.maxShooterRPM * Constants.maxShooterSpeed * 2048 / 600;
    //int loop = 0;

    /*if(loop < 1000){  //change to ANYTHING other a while loop later on (PID?)
      Shooter.set(ControlMode.PercentOutput,shooterSpeed);
      loop++;
    }else{
      loop = 0;
      Robot.hopper.spin(speed);
      Shooter.set(ControlMode.PercentOutput,shooterSpeed);

    }*/
    
  }

  public void autoShoot(int msWait){
    int timeoutLoop = msWait * 1;//change?? 
    int shootWait = Constants.AutoShootWait;

    Robot.vision.autoAlignWithTarget(timeoutLoop);
    
    Robot.hopper.spin(.2);

    while(shootWait > 0){//wait 3 sec, 
      try{
        Thread.sleep(1);
      }catch(Exception ex){

      }
      shootWait--;
    }

    roboShoot(false);
    Robot.hopper.spin(0);

    }

  

  public void updateShoot(){
    if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonB) && Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonTriggerRight)){
      roboShoot(false);
    }else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonB)){
      Robot.vision.alignWithTarget();
    }//update to turn off with a diff button?

  }

  public void actuate(){

    double motorPower;
    boolean isDone = false;

    //actuator.set(ControlMode.Position,Constants.maxPos);
    while(safety.get() > Constants.maxPos && timeThrough){
      actuator.set(ControlMode.PercentOutput, -1);
      actuateDisplay();
    }

    actuator.set(ControlMode.PercentOutput, 0);
    
    /*while(isDone == false && timeThrough){

      Robot.vision.updateVision();
      motorPower = Robot.vision.y * Constants.HomingModifier/27;

      SmartDashboard.putNumber("align motor power", motorPower);
      SmartDashboard.putNumber("shooter y", Robot.vision.y);

      actuator.set(ControlMode.PercentOutput, motorPower);

      if(Robot.vision.y > Constants.ShooterVisionError && safety.get() > Constants.safetyPos){ 
        isDone = false;
      }else if(Robot.vision.y < -Constants.ShooterVisionError && safety.get() > Constants.safetyPos){
        isDone = false;
      }else {
        isDone = true;

      }
    }*/

    //actuator.set(ControlMode.PercentOutput, 0);
    //safety.reset();
    
  }

  public void actuateDisplay(){
    int i = safety.get();
    SmartDashboard.putNumber("safety encoder", i);
  }

  public void actuateManuel(){
    if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonX)){
      actuator.set(ControlMode.PercentOutput, .5);
    }else if(Robot.oi.getControllerButtonStateOp(Constants.XBoxButtonY)){
      actuator.set(ControlMode.PercentOutput, -.5);
    }else{
      actuator.set(ControlMode.PercentOutput, 0);
    }
  }

  public void shooterActuate(){

  }

  public void returnHome(){

    if(time.get() >= Constants.shootEndTime && timeThrough){
      while(safety.get() < 200){
        actuator.set(ControlMode.PercentOutput, 1);
        actuateDisplay();
      }
      timeThrough = false;
      actuator.set(ControlMode.PercentOutput, 0);
    }

  }

  public void startTimer(){
    time.start();
  }

  public void endTimer(){
    time.stop();
  }

  public void resetTimer(){
    time.reset();
  }
  
  public void toggleShooter(){

    if(Robot.oi.getControllerButtonState(Constants.XBoxButtonB)){// && toggle == false){
      Robot.vision.alignWithTarget();
      toggle = true;
    }else if(Robot.oi.getControllerButtonState(Constants.XBoxButtonTriggerLeft)){// && toggle == true){
      roboShoot(false);
      //Robot.hopper.wackerSpinOff();
      Robot.hopper.spin(0);
      toggle = false;
      Robot.vision.ceaseRumble();
      Robot.vision.ledDisable();
    }

  }

  public double getShootSpeed(){
    return Shooter.getSelectedSensorVelocity();
  }
}
