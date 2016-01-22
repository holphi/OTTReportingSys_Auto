<html>
     <STYLE type="text/css">
        body {
            font:normal 90% Calibria,arial,helvetica;
            color:#000000;
        }
        table tr td, table tr th {
            font-size: 80%;
        }
        table.details tr th{
            font-weight: bold;
            text-align:left;
            background:#a6caf0;
        }
        table.details tr td{
            background:#eeeee0;
        }
        p {
            line-height:1.5em;
            margin-top:0.5em; margin-bottom:1.0em;
        }
        h1 {
            margin: 0px 0px 5px; font: bold 188% Calibria,arial,helvetica
        }
        h2 {
            margin-top: 1em; margin-bottom: 0.5em; font: bold 125% Calibri,arial,helvetica
        }
        h3 {
            margin-bottom: 0.5em; font: bold 115% Calibri,arial,helvetica
        }
        h4 {
            margin-bottom: 0.5em; font: bold 100% Calibri,arial,helvetica
        }
        h5 {
            margin-bottom: 0.5em; font: bold 100% Calibri,arial,helvetica
        }
        h6 {
            margin-bottom: 0.5em; font: bold 100% Calibri,arial,helvetica
        }
        .Error {
            font-weight:bold; color:red;
        }
        .Failure {
        font-weight:bold; color:green;
        }
        .Properties {
        text-align:right;
        }
   </STYLE>
  <body>
      <h1>Business & Security Monitoring Daily Automation Test Report (#TODAY#)</h1>
      <table width="100%">
        <tr>
            <td align="left"></td><td align="right">Tested by Teresa Tang</a>.</td>
        </tr>
      </table>
      <hr size="1">
    <table border="0" width="95%">
        <tr>
        <td style="text-align: justify;">
            Note: <em>failures</em> are anticipated and checked for with assertions while <em>errors</em> are unanticipated.
        </td>
        </tr>
    </table>
    <h2>Test Environment:</h2>
		 <table class="details" border="0", cellpadding="5" cellspacing="2" width="50%">
		 <tr>
		   <th colspan="3">Environment infomation:</td>
		 </tr>
		 <tr class="Failure">
		   <td rowspan="4"><a title="Display all tests">Server</a></td><td title="Display all tests"><a title="Display all tests">Hardware</a></td><td><a title="Display all tests">Virtual Machine (Oracle Virtual Box), memory size 1024M.</a></td>
		 </tr>
		 <tr class="Failure">
		  <td rowspan="3"><a title="Display all tests">Software</a></td><td><a title="Display all tests">OS: RedHat5.8 64bit</a></td>
		 </tr>
		 <tr class="Failure">
		 <td><a title="Display all tests">Application Server:JBoss430.6</a></td>
		 </tr>
		  <tr class="Failure">
		 <td><a title="Display all tests">JDK:jdk1.6.0_20 64bit</a></td>
		 </tr>
		 <tr class="Failure">
		 <td rowspan="3"><a title="Display all tests">Client</a></td><td><a title="Display all tests">Hardware</a></td><td><a title="Display all tests">Dell Desktop</a></td>
		 </tr>
		 <tr class="Failure">
		 <td rowspan="2"><a title="Display all tests">Software</a></td><td><a title="Display all tests">OS: Windows7 64bit</a></td>
		 </tr>
		 <tr class="Failure">
		 <td><a title="Display all tests">Browser: Internet Explorer 9</title></td>
		 </tr>
		 <tr class="Failure">
		 <td><a title="Display all tests">Test build</td>
		 <td colspan="2"><a title="Display all tests" href="#BUILD_URL#">#BUILD_URL#</a></td>
		 <tr>
		 </table>
      <h2>Test Summary:</h2>
        #SUMMARY#
    <h2>Test Details:</h2>
        #DETAIL#
    <h2>Automation Failures Chart:</h2>
    <img src="cid:failure" />&nbsp;&nbsp;&nbsp; <img src="cid:failurebypriority" />
	 <br><br>You can also check attached report for more detailed info. If you have any questions, please feel free to contact to NMP Beijing Testing Team!</br>
    </br>
    Thanks,</br>
    NMP Beijing Test Team</br>
  </body>
<html>