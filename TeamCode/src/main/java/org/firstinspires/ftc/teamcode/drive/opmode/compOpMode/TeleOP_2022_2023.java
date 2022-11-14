package org.firstinspires.ftc.teamcode.drive.opmode.compOpMode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.drive.opmode.compOpMode.Bases.BaseOpMode;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="hambunger", group="Linear Opmode")
public class TeleOP_2022_2023 extends BaseOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor drive_FL = null;
    private DcMotor drive_RL = null;
    private DcMotor drive_FR = null;
    private DcMotor drive_RR = null;
    private DcMotor vertiArm = null;
    private DcMotor horizArm = null;
    private DcMotor angleArm = null;
    private Servo horizClaw = null;
    private Servo transferClaw = null;
    private Servo transferArm = null;

    //public final static double ARM_DEFAULT = 0.5; //Unslash this if you want armTurn servo using joystick back
    public final static double ARM_MIN_RANGE = 0.46;
    public final static double ARM_MAX_RANGE = 0.53;

    //double armTurnPosition = ARM_DEFAULT;
    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        drive_FL = hardwareMap.get(DcMotor.class, "drive_FL");
        drive_RL = hardwareMap.get(DcMotor.class, "drive_RL");
        drive_FR = hardwareMap.get(DcMotor.class, "drive_FR");
        drive_RR = hardwareMap.get(DcMotor.class, "drive_RR");

        //armTurn.setPosition(ARM_DEFAULT); //Unslash this if you want armTurn servo using joystick back

        //Slow Mode Variables

        double SD = 1;
        double SA = 1;

        //Arm Turn Variables
        //double armTurn = ARM_DEFAULT;  //Unslash this if you want armTurn servo using joystick back
        final double ARM_SPEED = 0.005;

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the batter;
        drive_FL.setDirection(DcMotor.Direction.FORWARD);
        drive_RL.setDirection(DcMotor.Direction.FORWARD);
        drive_FR.setDirection(DcMotor.Direction.REVERSE);
        drive_RR.setDirection(DcMotor.Direction.REVERSE);

        //Drive Modes
        drive_FL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        drive_FR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        drive_RL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        drive_RR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        horizArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        vertiArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        angleArm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

          /*horizArm.setDirection(DcMotor.Direction.REVERSE);
       vertiArm.setDirection(DcMotor.Direction.FORWARD);
       angleArm.setDirection(DcMotor.Direction.FORWARD);*/  //we'll find out if we need these

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            //Show encoder values on the phone
            telemetry.addData("Status", "Initialized");
            telemetry.addData("Left Dead Encoder", drive_FL.getCurrentPosition());
            telemetry.addData("Right Dead Encoder", drive_RR.getCurrentPosition());
            telemetry.addData("Rear Dead Encoder", drive_RL.getCurrentPosition());
            telemetry.update();

            double y_stick = gamepad1.left_stick_y;
            double x_stick = gamepad1.left_stick_x;

            //Field Orientation Code
            double pi = 3.1415926;
            double gyro_degrees = navx_centered.getYaw();
            double gyro_radians = gyro_degrees * pi/180;
            double temp = y_stick * Math.cos(gyro_radians) + x_stick * Math.sin(gyro_radians);
            x_stick = -y_stick * Math.sin(gyro_radians) + x_stick * Math.cos(gyro_radians);

            //Mecanum Drive Code
            double r = Math.hypot(x_stick, y_stick);
            double robotAngle = Math.atan2(y_stick, -x_stick) - Math.PI / 4;
            double rightX = -gamepad1.right_stick_x;
            final double v1 = (r * Math.cos(robotAngle) + rightX);
            final double v2 = (r * Math.sin(robotAngle) - rightX);
            final double v3 = (r * Math.sin(robotAngle) + rightX);
            final double v4 = (r * Math.cos(robotAngle) - rightX);

            drive_FL.setPower(v1 * SD);
            drive_RL.setPower(v3 * SD);
            drive_FR.setPower(v2 * SD);
            drive_RR.setPower(v4 * SD);

            //Controller 1
            if (gamepad1.left_stick_button) {
                SD = .25;
            } else {
                SD = 1;
            }

            //Controller 2

            //Extends and Retracts horizArm
            if (gamepad2.x) {
                horizArm.setPower(.2);
            } else if (gamepad2.a) {
                horizArm.setPower(-.2);
            } else {
                horizArm.setPower(0);
            }

            //Opens and Closes claw
            if (gamepad2.y) {
                horizClaw.setPosition(.2);
            } else if (gamepad2.b) {
                horizClaw.setPosition(-.2);
            } else {
                horizClaw.setPosition(0);
            }

            //Moves vertArm up and down
            if (gamepad2.dpad_up){
                vertiArm.setPower(.2);
            } else if (gamepad2.dpad_down) {
                vertiArm.setPower(-.2);
            } else {
                vertiArm.setPower(0);
            }

            //Opens and Closes Transfer Claw
            if (gamepad2.left_bumper) {
                transferClaw.setPosition(.2);
            } else if (gamepad2.right_bumper) {
                transferClaw.setPosition(-.2);
            } else {
                transferClaw.setPosition(0);
            }

            //Moves anglearm up and down
            if (gamepad2.right_trigger > .5 ) {
                angleArm.setPower(.2);
            } else if (gamepad2.left_trigger > .5) {
                angleArm.setPower(-.2);
            } else {
                angleArm.setPower(0);
            }
        }
    }

    public int degreesBore(int input) {

        final int COUNTS_PER_BORE_MOTOR_REV = 8192;    // eg: GOBUILDA Motor Encoder
        int COUNTS_TICKS_PER_REV_PER_DEGREE = (COUNTS_PER_BORE_MOTOR_REV) / 360 * 2;

        return COUNTS_TICKS_PER_REV_PER_DEGREE * input;

    }
}