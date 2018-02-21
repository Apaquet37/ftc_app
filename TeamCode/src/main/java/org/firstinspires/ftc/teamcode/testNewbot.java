package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * ☺ Hi! Esto es el codigo para el 24 Febrero! ☺
 */
@TeleOp(name = "El Teleop", group = "bacon")
//@Disabled
public class testNewbot extends LinearOpMode {

    /* Declare all objects */
    DcMotor frontLeftMotor = null;
    DcMotor frontRightMotor = null;
    DcMotor backLeftMotor = null;
    DcMotor backRightMotor = null;
    DcMotor verticalArmMotor;
    DcMotor trayMotor;
    DcMotor P1Motor = null;
    DcMotor P2Motor = null;
    Servo gemServo;
    BNO055IMU imu;
    DigitalChannel topTouch;
    DigitalChannel bottomTouch;

    /* Give place holder values for the motors*/
    double FrontLeftPower = 0;
    double FrontRightPower = 0;
    double BackRightPower = 0;
    double BackLeftPower = 0;

    /*These values are used for collection and tray control*/
    double frontLeft;
    double frontRight;
    double backLeft;
    double backRight;
    double P1Power = -.7;
    double P2Power = .7;
    int trayOut = -390;
    int trayIn = 10;
    int trayPos = 0;
    int yPos = 0;
    int topPushed = 0;
    int bottomPushed = 0;
    boolean up;
    boolean xPush;
    boolean aPush = false;
    boolean yPush;


    /* Define values for teleop bumper control */
    static double nobumper = 1.5;
    static double bumperSlowest = 3.2;
    static double bumperFastest = 1.0;

    /* Define values used in knocking the jewels */
    static double xPosUp = 0;
    static double xPosDown = .5;

    @Override
    public void runOpMode() {

        /* Define and Initialize Hardware */
        frontLeftMotor = hardwareMap.dcMotor.get("FL");
        frontRightMotor = hardwareMap.dcMotor.get("FR");
        backLeftMotor = hardwareMap.dcMotor.get("BL");
        backRightMotor = hardwareMap.dcMotor.get("BR");
        verticalArmMotor = hardwareMap.dcMotor.get("VAM");
        gemServo = hardwareMap.servo.get("gemservo");
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        P1Motor = hardwareMap.dcMotor.get("P1");
        P2Motor = hardwareMap.dcMotor.get("P2");
        trayMotor = hardwareMap.dcMotor.get("TM");
        topTouch = hardwareMap.get(DigitalChannel.class, "TT");
        bottomTouch = hardwareMap.get(DigitalChannel.class, "BT");

        /* Initialize the tray's encoder */
        trayMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(100);
        trayMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        /* Set the digital channel to input. */
        topTouch.setMode(DigitalChannel.Mode.INPUT);
        bottomTouch.setMode(DigitalChannel.Mode.INPUT);


        /* Wait for the start button */
        telemetry.addLine("!☺ Ready to Run ☺!");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {

            /* Talk to the drivers & coach */
            telemetry.addLine("Hi~♪");
//            telemetry.addData("Wheel Control", "X is the toggle");
//            telemetry.addData("Tray Moving Controls", "Use the D-Pad ↑ & ↓ buttons!");
//            telemetry.addData("Tray Flipping Controls", "Hold A for out, release for in");
            telemetry.addData("topPushed", topPushed);
            telemetry.addData("bottomPushed", bottomPushed);
            telemetry.addData("Top Touch", topTouch.getState());
            telemetry.addData("Bottom Touch", bottomTouch.getState());
            telemetry.addData("yPos", yPos);
            telemetry.update();

            /* Put the servo arm up */
            gemServo.setPosition(xPosUp);

            /* Toggle the collection system */
            if (gamepad1.x) {
                if (Math.abs(P1Motor.getPower()) == 0 && !xPush) {
                    P1Motor.setPower(P1Power);
                    P2Motor.setPower(P2Power);
                } else {
                    if (Math.abs(P1Motor.getPower()) > 0 && !xPush) {
                        P1Motor.setPower(0);
                        P2Motor.setPower(0);
                    }
                }
                xPush = true;
            } else {
                xPush = false;
            }

            /* Function to toggle tray at bottom-most position */
            if (gamepad1.a) {
                if (trayPos == 0 & !aPush) {
                    trayMotor.setTargetPosition(trayOut);
                    trayMotor.setPower(-.4);
                    aPush = true;
                    trayPos++;
                } else {
                    if (trayPos == 1 && !aPush) {
                        trayMotor.setTargetPosition(trayIn);
                        trayMotor.setPower(.4);
                        aPush = true;
                        trayPos--;
                    }
                }
            } else {
                aPush = false;
            }

            /*** Int Toggle ***/
            if (gamepad1.y) {
                if (yPos == 0 && !yPush) {
                    yPos++;
                    yPush = true;
                } else {
                    if (yPos == 1 && !yPush) {
                        yPos--;
                        yPush = true;
                    }
                }
            } else {
                yPush = false;
            }

            /* Function to toggle tray at upper-most position */
            if (yPos == 1) {
                if (topPushed == 0) {
                    verticalArmMotor.setPower(1);
                    if (!topTouch.getState()) {
                        verticalArmMotor.setPower(0);
                        setTrayPosition("Out");
                        topPushed = 1;
                        bottomPushed = 0;
                    }
                }
            }

            if (yPos == 0) {
                if (bottomPushed == 0) {
                    verticalArmMotor.setPower(-.7);
                    if (!bottomTouch.getState()) {
                        verticalArmMotor.setPower(0);
                        setTrayPosition("In");
                        bottomPushed = 1;
                        topPushed = 0;
                    }
                }
            }

            if (yPos == 0 && bottomTouch.getState() && getRuntime() < 5) {
                if (bottomPushed == 0) {
                    verticalArmMotor.setPower(-.2);
                    if (!bottomTouch.getState()) {
                        verticalArmMotor.setPower(0);
                        trayMotor.setPower(.1);
                        bottomPushed = 1;
                        topPushed = 0;
                    }
                }
            }

            /* Rotational Control */
            if (gamepad1.left_bumper && gamepad1.right_stick_x < 0 || gamepad1.left_bumper && gamepad1.right_stick_x > 0) {

                double GRX = gamepad1.right_stick_x / bumperSlowest;

                final double v1 = +GRX;
                final double v2 = -GRX;
                final double v3 = +GRX;
                final double v4 = -GRX;

                frontLeft = -v1;
                frontRight = v2;
                backLeft = -v3;
                backRight = v4;

                setWheelPower(frontLeft, frontRight, backLeft, backRight);
            } else {

                if (gamepad1.right_bumper && gamepad1.right_stick_x < 0 || gamepad1.right_bumper && gamepad1.right_stick_x > 0) {

                    double GRX = gamepad1.right_stick_x / bumperFastest;

                    final double v1 = +GRX;
                    final double v2 = -GRX;
                    final double v3 = +GRX;
                    final double v4 = -GRX;

                    frontLeft = -v1;
                    frontRight = v2;
                    backLeft = -v3;
                    backRight = v4;

                    setWheelPower(frontLeft, frontRight, backLeft, backRight);

                } else {

                    double GRX = gamepad1.right_stick_x / nobumper;

                    final double v1 = +GRX;
                    final double v2 = -GRX;
                    final double v3 = +GRX;
                    final double v4 = -GRX;

                    frontLeft = -v1;
                    frontRight = v2;
                    backLeft = -v3;
                    backRight = v4;

                    setWheelPower(frontLeft, frontRight, backLeft, backRight);
                }
            }
        /* Drive Control */
            if (gamepad1.left_bumper) {
                double GLY = -gamepad1.left_stick_y / bumperSlowest;
                double GRX = gamepad1.right_stick_x / bumperSlowest;
                double GLX = gamepad1.left_stick_x / bumperSlowest;

                final double v1 = GLY + GRX + GLX;
                final double v2 = GLY - GRX - GLX;
                final double v3 = GLY + GRX - GLX;
                final double v4 = GLY - GRX + GLX;

                frontLeft = -v1;
                frontRight = v2;
                backLeft = -v3;
                backRight = v4;

                setWheelPower(frontLeft, frontRight, backLeft, backRight);
            } else {
                if (gamepad1.right_bumper) {
                    double GLY = -gamepad1.left_stick_y / bumperFastest;
                    double GRX = gamepad1.right_stick_x / bumperFastest;
                    double GLX = gamepad1.left_stick_x / bumperFastest;

                    final double v1 = GLY + GRX + GLX;
                    final double v2 = GLY - GRX - GLX;
                    final double v3 = GLY + GRX - GLX;
                    final double v4 = GLY - GRX + GLX;

                    frontLeft = -v1;
                    frontRight = v2;
                    backLeft = -v3;
                    backRight = v4;

                    setWheelPower(frontLeft, frontRight, backLeft, backRight);

                } else {
                    double GLY = -gamepad1.left_stick_y / nobumper;
                    double GRX = gamepad1.right_stick_x / nobumper;
                    double GLX = gamepad1.left_stick_x / nobumper;

                    final double v1 = GLY + GRX + GLX;
                    final double v2 = GLY - GRX - GLX;
                    final double v3 = GLY + GRX - GLX;
                    final double v4 = GLY - GRX + GLX;

                    frontLeft = -v1;
                    frontRight = v2;
                    backLeft = -v3;
                    backRight = v4;

                    setWheelPower(frontLeft, frontRight, backLeft, backRight);

                }
            }
        }
    }

    /***********************************************************************************************
     * These are all of the methods used in the Teleop*
     ***********************************************************************************************/

    /* This method moves the tray to position */
    public void setTrayPosition(String position) {
        switch (position) {
            case "Out":
                trayMotor.setTargetPosition(trayOut);
                trayMotor.setPower(-.4);
                break;
            case "In":
                trayMotor.setTargetPosition(trayIn);
                trayMotor.setPower(.4);
                break;
        }
    }

    /* This method powers each wheel to whichever power is desired */
    public void setWheelPower(double fl, double fr, double bl, double br) {

        /* Create power variables */
        double frontLeft;
        double frontRight;
        double backLeft;
        double backRight;

        /* Initialize the powers with the values input whenever this method is called */
        frontLeft = fl;
        frontRight = fr;
        backLeft = bl;
        backRight = br;

        /* set each wheel to the power indicated whenever this method is called */
        if (FrontLeftPower != frontLeft) {
            frontLeftMotor.setPower(fl);
            FrontLeftPower = frontLeft;
        }
        if (FrontRightPower != frontRight) {
            frontRightMotor.setPower(fr);
            FrontRightPower = frontRight;
        }
        if (BackLeftPower != backLeft) {
            backLeftMotor.setPower(bl);
            BackLeftPower = backLeft;
        }
        if (BackRightPower != backRight)
            backRightMotor.setPower(br);
        BackRightPower = backRight;
    }
}
