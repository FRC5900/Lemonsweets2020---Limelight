/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
//import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.cameraserver.*;
//import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * This is a demo program showing the use of the RobotDrive class, specifically
 * it contains the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {
  private DifferentialDrive m_myRobot;
  
  public static boolean LimelightHasValidTarget = false;
	public static double LimelightDriveCommand = 0.0;
	public static double LimelightSteerCommand = 0.0;
  final double STEER_K = 0.025;
  final double DRIVE_K = 0.03;
  final double DESIRED_TARGET_AREA = 13.0;
  final double MAX_DRIVE = 0.7;
  double tv;
  double tx;
  double ty;
  double ta;
  double steer_cmd;

  private Joystick m_rightStick;
  private Joystick m_leftStick;
  private Talon m_intake;
  private Spark m_shooterLeft;
  private Spark m_shooterRight;
  

  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
   
    m_myRobot = new DifferentialDrive(new Talon(7), new Talon(8));
    m_intake = new Talon(9);
    m_shooterLeft = new Spark(6);
    m_shooterRight = new Spark(5);
   
    m_rightStick = new Joystick(0);
    m_leftStick = new Joystick(1);
   
  
  }

  @Override
  public void teleopPeriodic() {
    
   // double turningValue = (kAngleSetpoint - m_gyro.getAngle()) * kP;
    // Invert the direction of the turn if we are going backwards
    //turningValue = Math.copySign(turningValue, m_rightStick.getY());
    

    double shooterspeed;
    double drivespeed;
    double turnspeed;
    //Boolean Quickturn = m_rightStick.getRawButton(7);
   if (m_rightStick.getRawButton(1)){
      Update_Limelight_Tracking();
      m_myRobot.curvatureDrive(LimelightDriveCommand, LimelightSteerCommand*0.5, true);
   }
   else
   {
      drivespeed = -m_rightStick.getY();
      turnspeed = m_rightStick.getX();
      m_myRobot.curvatureDrive(drivespeed/2, turnspeed/1.5, true);
   }
   
    //m_myRobot.tankDrive(leftspeed , rightspeed);

    if (m_rightStick.getRawButton(2))
      m_intake.set(0.5); 
    else 
      m_intake.set(0);

    shooterspeed = m_leftStick.getY();
    m_shooterRight.set(-shooterspeed*0.7);
    m_shooterLeft.set(shooterspeed*0.7);
  }


  public void Update_Limelight_Tracking()
  {  
    tv = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tv").getDouble(0);
    tx = NetworkTableInstance.getDefault().getTable("limelight").getEntry("tx").getDouble(0);
    ty = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ty").getDouble(0);
    ta = NetworkTableInstance.getDefault().getTable("limelight").getEntry("ta").getDouble(0);
  
    if (tv < 1.0){
      LimelightHasValidTarget = false;
      LimelightDriveCommand = 0.0;
      LimelightSteerCommand = 0.0;
      return;
    }
    else {
      LimelightHasValidTarget = true;
      LimelightSteerCommand = tx * STEER_K;
      LimelightDriveCommand = -ty * DRIVE_K;     
    }
  }
}
