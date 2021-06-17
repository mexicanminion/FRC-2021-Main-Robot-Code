// Harry May was here
// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.*;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //creates subsystem objects
  public static OI oi;
  public static DriveBase driveBase;
  public static Hopper hopper;
  public static Shooter shooter;
  public static Intake intake;
  public static Pneumatics air;
  public static Accelerometer imu;
  public static Vision vision;
  public static Winch winch;
  public static Hand leftStick = Hand.kLeft;
  public static Hand rightStick = Hand.kRight;  

  /** 
  * updates SmartDashboard (needs to be called periodically)
  */
  private void updateSmartDashboard(){
    
    SmartDashboard.putNumber("Left Joy", oi.getControllerStickLeft());
    SmartDashboard.putNumber("Right Joy", oi.getControllerStickRight());
    SmartDashboard.putNumber("vision x", Robot.vision.x);

  }
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    oi = new OI();
    driveBase = new DriveBase();
    hopper = new Hopper();
    shooter = new Shooter();
    intake = new Intake();
    imu = new Accelerometer();
    vision = new Vision();
    air = new Pneumatics();
    winch = new Winch();

  
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {
    driveBase.parkingBrake(false);
    driveBase.fullStop();
    vision.ledDisable();
  }

  /** 
   * called periodically once disabled 
   */
  @Override
  public void disabledPeriodic() {
    vision.ledDisable();
    vision.ceaseRumble();
  }

  /** 
   * This autonomous runs the autonomous once 
   */
  @Override
  public void autonomousInit() {

    vision.ceaseRumble();
    driveBase.zeroEncoders();
    air.setIntakePiston(true); 
    roboAuto(1);
    vision.ceaseRumble();
    //driveBase.runToPos(-10, -.5);
    //imu.resetPigeonYaw();
    //driveBase.zeroEncoders();
    //roboAuto(1);

  }

  /** 
   * This function is called periodically during autonomous. 
   */
  @Override
  public void autonomousPeriodic() {
    driveBase.fullStop();
  }

  /**
   * beginning of teleop, ran once
   */
  @Override
  public void teleopInit() {

    imu.resetPigeonYaw();
    driveBase.parkingBrake(true);
    air.setIntakePiston(false);
    driveBase.setVelFlag(false);
    hopper.wackerSpinOn();
    oi.zeroJoySticks();
    shooter.startTimer();
  }

  /** 
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {

    vision.ledEnable();
    driveBase.updateDriveBase();//joyticks,  DRIVER , hold a to speed boost(commented out)
    intake.teleIntake();//a, a + rigth bummper to reverse, OP
    air.intakeContoller();// y  to lift, x intake, right bummer + button will reverse, OP
    winch.winchUp();// dpad up OP
    shooter.toggleShooter(); //B to smart shoot, home/back button to escape, left bummper to stop shooter motor and hopper, DRIVER
    hopper.manuelSpin();//b to go feed in, right bumper + B to reverse(un-jam), OP

    updateSmartDashboard();



    //obsolete code (code not used/debug code)

    /*shooter.manuelShoot();
    hopper.wackerToggle();//d-pad down
    shooter.actuateManuel();
    shooter.updateShoot();//b, b and r bumpper to turn off shooter, home to eascape loop 
    shooter.returnHome();
    */

  }


  /**
   * runs once for test mode
   */
  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
  }

  /** 
   * This function is called periodically during test mode. 
   */
  @Override
  public void testPeriodic() {

    vision.ledEnable();
    shooter.actuateDisplay();
    //shooter.actuate();
    //shooter.returnHome();
    shooter.actuateManuel();
  }


  /**
   * Main robot Auto code, Wrapper method for auto 
   * @param feildPos obsolete, was used to dictate field position
   */
  private void roboAuto(int feildPos){

    vision.ledEnable();
    vision.updateVision();

    //used to delay shooter for vision to boot, 1 sec delay
    for(int i = 0; i <= 1000; i++){

      try{
        Thread.sleep(1);
      }catch(Exception ex){

      }
    }

    facingPowerPort();

    //unused code
    /*
    // 1 is center
    //driveBase.setVelFlag(true);
    if(feildPos == 1){
      //facingPowerPort();
    }else if(feildPos == 2){
      //here
    }
    //driveBase.setVelFlag(false);
    */
  }

  /**
   * Auto code used for facing Power Port
   */
  private void facingPowerPort(){
    shooter.autoShoot(1000);
    driveBase.runToPos(-8, -.3);
  }
 /**
   * Auto code used for just getting out of the way, aka move
   */
  private void move(){
    driveBase.runToPos(-8, .3);
  }

  /*
  code never developed :(

  private void grabBalls(){
    shooter.autoShoot(8);

  }*/


}
