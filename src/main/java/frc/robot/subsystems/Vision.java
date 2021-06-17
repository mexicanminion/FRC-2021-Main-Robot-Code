// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;
import frc.robot.Robot;


import edu.wpi.first.cameraserver.*;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Constants;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision extends SubsystemBase {

    //private static SerialPort JeVoisVision;
    private static String CommandName = "Vision";
    /*private static boolean jeVoisAlive = false;
    private static String rXString = "";
    private static double xPosition = 0; 
    private static double yPosition = 0;
    private static double xPositionTemp = 0; 
    private static double yPositionTemp = 0;
    private static boolean isBusy = false;
    private static int timeoutCounter = 0;*/
    private CameraServer cam1;
    private CameraServer cam2;
  
    NetworkTableEntry tx;
    NetworkTableEntry ty;
    NetworkTableEntry ta;
    double txSim;
    double tySim;
    double taSim;
  
    public double x;
    public double y;
    double area;
  
    boolean rumbleOnce = false;
    int rumbleCounter = 0;
  
    //Servo visionServo;
    boolean servoToggle = false;
    static NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
  
  /** Creates a new Vision. */
  public Vision() {

    ledDisable();
    tx = table.getEntry("tx");
    ty = table.getEntry("ty");
    ta = table.getEntry("ta");
    //visionServo = new Servo(Constants.VisionServoPort);
    cam1.getInstance().startAutomaticCapture(0);
    //cam2.getInstance().startAutomaticCapture(1);


  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  public void updateVision(){
    //read values periodically
      ledEnable();
      x = tx.getDouble(0.0);
      y = ty.getDouble(0.0);
      area = ta.getDouble(0.0);
      SmartDashboard.putNumber("tx", x);
      SmartDashboard.putNumber("ty", y);
      SmartDashboard.putNumber("area", area);
    
    if(Math.abs(x) < Constants.VisionErrorAllowed)
    {  
      goRumble = 1;
    }
    else 
    {
      goRumble = 0;
    }
    rumbler();
  } 
  
  public double estimateDistancePowerPort(){ //Note: CANNOT use this method for Loading Bay. The Limelight is most likely too close in height to the Loading Bay target to make this method effective.
    double distance = (Constants.PowerPortTargetHeight-Constants.LimelightMountHeight) / Math.tan(Constants.LimelightMountAngle + x);
    return distance;
  }

  static int RumbleState = 0;
  static int lastGoRumble = 0;
  static int goRumble = 0;

  public void rumbler(){
    if ((goRumble == 1) && (lastGoRumble == 0) && (RumbleState == 0))
    {
      startRumble();
      rumbleCounter = Constants.rumbleCount;
      RumbleState = 1;
    }

    if (RumbleState == 0)
    {
      SmartDashboard.putString("Rumble Status", "In indefinite hold");
    }//Rumbling done
    else if (RumbleState == 1) //Rumbling on first time
    {
      SmartDashboard.putString("Rumble Status", "RumbleRumble 1");
      if (rumbleCounter != 0)
        rumbleCounter --;
      else
      {
        ceaseRumble();
        RumbleState = 2;//Wait some time with rumble off
        rumbleCounter = Constants.rumbleCountWait;
      }
    }
    else if (RumbleState == 2)//Rumbling off for first time
    { 
      SmartDashboard.putString("Rumble Status", "No RumbleRumble");
      if (rumbleCounter != 0)
        rumbleCounter --;
      else
      {
        //startRumble();
        RumbleState = 3;//Rumbling on for second time
        rumbleCounter = Constants.rumbleCount;
      }
    }
    else if (RumbleState == 3)//Rumbling off for second time
    if (rumbleCounter != 0)
        rumbleCounter --;
    else
    {
      SmartDashboard.putString("Rumble Status", "RumbleRumble 2");
      ceaseRumble();
      RumbleState = 0;//Rumbling done, wait for new start
    }

    lastGoRumble = goRumble;
/*
    if(rumbleCounter > 0 && rumbleCounter < 50 || rumbleCounter > 100 && rumbleCounter < 150){
      Robot.operatorInterface.driveJoystick.setRumble(RumbleType.kLeftRumble, 1);
      Robot.operatorInterface.driveJoystick.setRumble(RumbleType.kRightRumble, 1);
    } if(rumbleCounter > 50 && rumbleCounter < 100 || rumbleCounter > 150){
      Robot.operatorInterface.driveJoystick.setRumble(RumbleType.kLeftRumble, 0);
      Robot.operatorInterface.driveJoystick.setRumble(RumbleType.kRightRumble, 0);
    } if(rumbleCounter > 150) {
      rumbleOnce = true;
    }*/
  }

  public void ceaseRumble(){
    Robot.oi.driveJoystick.setRumble(RumbleType.kLeftRumble, 0);
    Robot.oi.opJoystick.setRumble(RumbleType.kLeftRumble, 0);
  }

  public void startRumble(){
    Robot.oi.driveJoystick.setRumble(RumbleType.kLeftRumble, 1);
    Robot.oi.opJoystick.setRumble(RumbleType.kLeftRumble, 1);
  }
  /*public void servoTilt(double angle) {
    visionServo.setAngle(angle);
  }*/

  public void ledDisable(){
    table.getEntry("ledMode").setNumber(1);
  }

  public void ledEnable(){
    table.getEntry("ledMode").setNumber(3);
  }

  public void alignWithTarget(){
    boolean isDone = false;
    Robot.shooter.roboShoot(true); //add back after done testing aligning
    Robot.driveBase.parkingBrake(true);
    double motorPower;
    double powerFudge = 0;

    while (isDone == false) {//instead of isDone, add XBoxB
      SmartDashboard.putString("align?", "going");
      updateVision();
      SmartDashboard.putBoolean("Shooter loop", true);
      SmartDashboard.putNumber("vision x", x);
      motorPower = x * Constants.HomingModifier/27;
      if(Math.abs(motorPower) < 0.1){
        if(motorPower < 0){
          powerFudge = -0.08;
        }else{
          powerFudge = 0.08;
        }
        
      }
      SmartDashboard.putNumber("motor power (vision)", motorPower);
      Robot.driveBase.drive(-motorPower-powerFudge, motorPower+powerFudge);

      if(Robot.vision.x > Constants.VisionErrorAllowed){ 
        //Robot.driveBase.drive((Math.abs(Robot.vision.x) / 27) * Constants.HomingModifier, (Math.abs(Robot.vision.x) / -27) * Constants.HomingModifier);
        isDone = false;
      }else if(Robot.vision.x < -Constants.VisionErrorAllowed){
        //Robot.driveBase.drive((Math.abs(Robot.vision.x) / -27) * Constants.HomingModifier, (Math.abs(Robot.vision.x) / 27) * Constants.HomingModifier);
        isDone = false;
      }else {
        isDone = true;
        //Robot.hopper.wackerSpinOn();
        Robot.hopper.spin(.3);
        startRumble();
      }

      if(Robot.oi.getControllerButtonState(Constants.XBoxButtonHome)){
        Robot.shooter.roboShoot(false);
        Robot.driveBase.drive(0, 0);
        ceaseRumble();
        ledDisable();
        isDone = true;
      }

    }

    //Robot.shooter.roboShoot(false);
    SmartDashboard.putString("align?", "done");
    SmartDashboard.putBoolean("Shooter loop", false);

    SmartDashboard.putNumber("vision x", x);
    
    Robot.driveBase.drive(0, 0);

  }

  public void autoAlignWithTarget(int escape){
    boolean isDone = false;
    Robot.shooter.roboShoot(true);
    System.out.println("shooter turned on");
    Robot.driveBase.parkingBrake(true);
    double motorPower;
    double powerFudge = 0;

    while (isDone == false) {
      SmartDashboard.putString("auto progress", "going");
      System.out.println("auto progress going");
      updateVision();
      escape--;
      SmartDashboard.putNumber("vision x", x);
      motorPower = x * Constants.HomingModifier/27;
      if(Math.abs(motorPower) < 0.1){
        if(motorPower < 0){
          powerFudge = -0.05;
        }else{
          powerFudge = 0.05;
        }
        
      }
      SmartDashboard.putNumber("motor power (vision)", motorPower);
      Robot.driveBase.drive(-motorPower-powerFudge, motorPower+powerFudge);

      if(Robot.vision.x > Constants.VisionErrorAllowed){ 
        //Robot.driveBase.drive((Math.abs(Robot.vision.x) / 27) * Constants.HomingModifier, (Math.abs(Robot.vision.x) / -27) * Constants.HomingModifier);
        isDone = false;
        System.out.println("auto progress check 1");
      }else if(Robot.vision.x < -Constants.VisionErrorAllowed){
        //Robot.driveBase.drive((Math.abs(Robot.vision.x) / -27) * Constants.HomingModifier, (Math.abs(Robot.vision.x) / 27) * Constants.HomingModifier);
        isDone = false;
        System.out.println("auto progress check 2");

      }else {
        isDone = true;
        Robot.hopper.wackerSpinOn();
        //ledDisable();
        SmartDashboard.putString("auto progress", "complete 1");
        System.out.println("auto progress done");
        Robot.driveBase.drive(0, 0);
      }

      if(escape <= 0){
        Robot.shooter.roboShoot(false);//keep?????
        Robot.driveBase.drive(0, 0);
        isDone = true;
        System.out.println("ROBOT ESCAPED!!!!!!!!");
      }

    }
    SmartDashboard.putNumber("vision x", x);

    
    Robot.driveBase.drive(0, 0);
    SmartDashboard.putString("auto progress", "complete 2");


  
  }

  


}
