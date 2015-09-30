# About MTS #
Multi-protocol test suite specially designed for IP network :

  * IMS architecture (SIP, RTP, RTPFLOW, DIAMETER, HTTP) +
  * EPC/LTE architecture (GTP-C.V2, GTP-C.V1, GTP-U, GTP prime) +
  * application ones (RTSP, H248, MSRP, RADIUS, SMTP, POP, IMAP, SMPP, UCP, RADIUS, SIGTRAN, SNMP, …) +
  * basic transport protocols (TCP, UDP, SCTP, TLS) +
  * ETHERNET capture and sending +

Functional, non-regression or protocol tests => Sequential mode
Performance endurance stress tests => Load mode
Simulates equipments => the client, server or both sides
System supervision  => Capture mode (like wireshark)

Definition of tests case in XML files : test and scenarios input files
Graphical (very convivial) or command line (for test automation) user interfaces
Report the test running : logging function and rich statistics presentation.

Pure software solution (use commercial IP network card) => support only IP based protocol (for PSTN combined with Asterisk acting as a gateway)
Written in java => support of many famous platforms : Windows and Linux supported

Possible to run a test remotely with the master/slave feature

To have more information about MTS please look at the following link : http://mts.arm-tool.com

# Wiki pointers #

How to build in trunk : [build](build.md)

How to build in mts-5.5 branch : [build\_maven](build_maven.md)
