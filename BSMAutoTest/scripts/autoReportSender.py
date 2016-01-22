'''
Created on Dec 25, 2012
@author: Alex LI

'''

import os,sys
import smtplib
import genReport
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from email.mime.base import MIMEBase
from email.encoders import encode_base64

def getSmtpConfig():
    results=[]
    try:        
        fileHandler = open(os.path.join(sys.path[0],'smtp.cfg'))
        cfg_items = fileHandler.read().split('\n')
        for item in cfg_items:
            i = item.split('=')
            results.append([i[0],i[1]])
        return dict(results)
    finally:
        fileHandler.close()

smtpConfig = getSmtpConfig()

def composeReport():
    import time, os.path
    
    str_date = time.strftime('%Y/%m/%d',time.localtime(time.time()))
    subject = '[BSM] Business & Security Monitoring Daily Automation Test Report(%s)' %(str_date)
    #Create Message data
    msgbody = MIMEMultipart()
    #msgbody = MIMEMultipart('alternative')
    msgbody['Subject'] = subject
    msgbody['From'] =  smtpConfig['sender']
    msgbody['To'] = smtpConfig['receiver']
    
    try:
        #Append Automation test report
        fileHandler = open(os.path.join(sys.path[0],'msg.result'))
        bodytext = fileHandler.read()
        body = MIMEText(bodytext, _subtype='html')
        msgbody.attach(body)

        #Append failure chart
        imgdata = open(os.path.join(sys.path[0],'Failure.png'), 'rb').read()
        img = MIMEImage(imgdata, 'png')
        img.add_header('Content-Id', '<failure>')
        msgbody.attach(img)

        #Append failure by priority chart
        imgdata = open(os.path.join(sys.path[0],'FailureByPriority.png'),'rb').read()
        img = MIMEImage(imgdata, 'png')
        img.add_header('Content-Id', '<failurebypriority>')
        msgbody.attach(img)
        
        #Attach report
        contype = 'application/octet-stream'
        maintype, subtype = contype.split('/',1)
        rptdata = open(os.path.join(sys.path[0],'..','junit','BSMTestResult.zip'),'rb')
        rpt = MIMEBase(maintype, subtype)
        rpt.set_payload(rptdata.read())
        rptdata.close()
        rpt['Content-Disposition'] = 'attachment;filename="BSMTestResult.zip"'
        encode_base64(rpt)
        msgbody.attach(rpt)
        return msgbody
    except Exception:
        raise
        
def sendReport():
    receiverItems = smtpConfig['receiver'].split(';')
    receiverList = []
    for item in receiverItems:
        receiverList.append(item)
    body = composeReport()
    
    try:
        smtp = smtplib.SMTP()
        smtp.connect(smtpConfig['smtp'])
#        smtp.login(smtpConfig['user'], smtpConfig['pwd'])
        smtp.sendmail(smtpConfig['sender'], receiverList, body.as_string())
    except Exception:
        print 'Error happened in sendReport'
        raise
    finally:
        smtp.close()
        
if __name__ == '__main__':
    sendReport()
