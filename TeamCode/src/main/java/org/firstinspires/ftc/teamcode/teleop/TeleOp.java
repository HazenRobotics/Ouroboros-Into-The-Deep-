package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.drivetrain.MechDrive;
import org.firstinspires.ftc.teamcode.roadrunner.tuning.MecanumDrive;
import org.firstinspires.ftc.teamcode.subsystems.Arm;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Climb;
import org.firstinspires.ftc.teamcode.roadrunner.tuning.GoBildaPinpointDriver;
import org.firstinspires.ftc.teamcode.subsystems.Imu;
import org.firstinspires.ftc.teamcode.subsystems.Lift;
import org.firstinspires.ftc.teamcode.subsystems.Limelight;
import org.firstinspires.ftc.teamcode.utils.DriverHubHelp;
import org.firstinspires.ftc.teamcode.utils.GamepadEvents;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Locale;

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name ="AAurabot TeleOp")
public class TeleOp extends LinearOpMode {
    private GamepadEvents controller1, controller2;
    private MechDrive robot;
    private Limelight limelight;
    private Imu imu;
//    private ThreeDeadWheelLocalizer deadwheels;
    private DriverHubHelp screen;
    GoBildaPinpointDriver odometry;
    private Arm arm;
    private double armPos;
    private int liftPos;
    private Claw claw;
    private double clawPos;
    public boolean clawIn = false;
    private Lift lift;
    private int expectedManualLiftPos;
    boolean reverseArm;
    private double[] armPositions = {0.7, 0.9, 1.0, 0.3, 0};
    private int[] liftPositions = {-1000, -4000, 1000};
    private int armPositionIndex = 0;
    private int liftPositionIndex = 0;
    private boolean isReversing = false;
    private Climb climb;
    private boolean fieldCentric = false;
    double oldTime = 0;
    private final double liftPower = 0.1;
    boolean armIsUp = true;
    double midArmPos;
    Lift manuaLift;

    double headingOffsetDeg = 0;
    int testPos = 0;
    public void runOpMode() throws InterruptedException{

        expectedManualLiftPos = 0;
        controller1 = new GamepadEvents(gamepad1);
        controller2 = new GamepadEvents(gamepad2);
        robot = new MechDrive(hardwareMap);
        limelight = new Limelight(hardwareMap);
        IMU imu = hardwareMap.get(IMU.class, "imu");
        screen = new DriverHubHelp();
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0));
        claw = new Claw(hardwareMap);
//        lift = new Lift(hardwareMap, "liftLeft", "liftRight", "liftLeft", "liftRight" );
        reverseArm = false;
        odometry = hardwareMap.get(GoBildaPinpointDriver.class, "odo");
//       climb = new Climb(hardwareMap,"climb");
        fieldCentric = false;
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
        ));

        imu.initialize(parameters);

        //setting up odometry
        odometry.setOffsets(-84.0, -168.0, headingOffsetDeg);
        odometry.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        odometry.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        odometry.recalibrateIMU();
        odometry.resetPosAndIMU();

        telemetry.addData("Status", "Initialized");
        telemetry.addData("X offset", odometry.getXOffset());
        telemetry.addData("Y offset", odometry.getYOffset());
        telemetry.addData("Device Version Number:", odometry.getDeviceVersion());
        telemetry.addData("Device Scalar", odometry.getYawScalar());

        waitForStart();
        resetRuntime();

        while(opModeIsActive())
        {

            //end of field centric
            //odometry stuff

            odometry.update();
            /*
            This code prints the loop frequency of the REV Control Hub. This frequency is effected
            by I²C reads/writes. So it's good to keep an eye on. This code calculates the amount
            of time each cycle takes and finds the frequency (number of updates per second) from
            that cycle time.
             */
            double newTime = getRuntime();
            double loopTime = newTime - oldTime;
            double frequency = 1 / loopTime;
            oldTime = newTime;

             /*
            gets the current Position (x & y in mm, and heading in degrees) of the robot, and prints it.
             */
            Pose2D pos = odometry.getPosition();
            String data = String.format(Locale.US, "{X: %.3f, Y: %.3f, H: %.3f}", pos.getX(DistanceUnit.MM), pos.getY(DistanceUnit.MM), pos.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Position", data);

            /*
            gets the current Velocity (x & y in mm/sec and heading in degrees/sec) and prints it.
             */
            Pose2D vel = odometry.getVelocity();
            String velocity = String.format(Locale.US,"{XVel: %.3f, YVel: %.3f, HVel: %.3f}", vel.getX(DistanceUnit.MM), vel.getY(DistanceUnit.MM), vel.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Velocity", velocity);

             /*
            Gets the Pinpoint device status. Pinpoint can reflect a few states. But we'll primarily see
            READY: the device is working as normal
            CALIBRATING: the device is calibrating and outputs are put on hold
            NOT_READY: the device is resetting from scratch. This should only happen after a power-cycle
            FAULT_NO_PODS_DETECTED - the device does not detect any pods plugged in
            FAULT_X_POD_NOT_DETECTED - The device does not detect an X pod plugged in
            FAULT_Y_POD_NOT_DETECTED - The device does not detect a Y pod plugged in
            */
            telemetry.addData("Status", odometry.getDeviceStatus());

            telemetry.addData("Pinpoint Frequency", odometry.getFrequency()); //prints/gets the current refresh rate of the Pinpoint

            telemetry.addData("REV Hub Frequency: ", frequency); //prints the control system refresh rate
            //Driver controls

            telemetry.addData("Field Centric: ", fieldCentric);

            //drive
            double forward = controller1.left_stick_y;
            double strafe = controller1.left_stick_x;
            double rotate = -controller1.right_stick_x;
            clawPos = 0.6;
            YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();

            //drive and limelight
//            String[] distance =limelight.getDistanceInInches();
//            telemetry.addData("Limelight Distance: ", distance[0] + ", " + distance[1]);
//            drive.updatePoseEstimate();

            robot.drive(forward, strafe, rotate);

//            telemetry.addData("x", screen.roundData(drive.pose.position.x));
//            telemetry.addData("y", screen.roundData(drive.pose.position.y));
//            telemetry.addData("Yaw (deg)", screen.roundData(Math.toDegrees(drive.pose.heading.toDouble())));




            //lift

            //manual mode for lift
//            double liftTriggerValue = controller2.left_trigger.getTriggerValue() - controller2.right_trigger.getTriggerValue();
//            manuaLift.setPower(liftTriggerValue);
            telemetry.addData("Expected Target Pos", expectedManualLiftPos);

            //claw
            if (controller1.b.onPress()) {
                claw.toggle();
                telemetry.addData("Claw is Open: ", claw.getIsOpen());
            }

//            telemetry.addData("Lift Left pos", lift.getLeftPosition());
//            telemetry.addData("Lift Right pos", lift.getRightPosition());
            telemetry.addData("Arm Pos", armPos);
            telemetry.update();
            controller1.update();


        }


    }

}

