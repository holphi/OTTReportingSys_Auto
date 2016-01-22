import paramiko, os
from nmplogging import getLogger

def getConfigData():
    results = {}
    from xml.etree import ElementTree as ET
    str = os.path.join(os.getcwd(),'config','BSMConfig.xml')
    getLogger().info('Result from Ant command: %s' %(str))
    config = ET.parse(os.path.join(os.getcwd(),'config','BSMConfig.xml'))
    entries = config.findall('./entry')
    for entry in entries:
        results[entry.attrib['key']]=entry.text
    return results

def getSSHConn():
    try:
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(BSMCONFIG['host_name'], 22,
                    BSMCONFIG['host_username'],
                    BSMCONFIG['host_password'])
        return ssh
    except Exception:
        raise   

def getHttpResponse(url):
    import urllib
    try:
        return urllib.urlopen(url).read()
    except Exception:
        return ''

#return build num and build address
def getBuildInfo():
    import re
    buildnum = ''
    url = ''
    try:
        #Get build num
        html = getHttpResponse(BSMCONFIG['buildbranch'])
        patt = re.compile(r'Last successful build \(#(\d+)\)')
        matcher = patt.search(html)
        if matcher is not None:
            buildnum = matcher.group(1)
        #Compose build download address
        url = BSMCONFIG['buildbranch'] + buildnum + '/artifact/bsmapp/target/' + BSMCONFIG['artifact']
        getLogger().info('Latest successful build num is %s and url %s' %(buildnum, url))
        return buildnum, url
    except Exception:
        raise

def downloadBuild():
    #1. Remove previous installation package
    getLogger().info('Remove previous package')
    build = re.search('-(.*)_', BSMCONFIG['artifact']).group(1)
    cmd = 'rm -f *%s*' % build
    runSSHCommand(cmd)        
    #2. Retrive and download the latest build info
    getLogger().info('Retrieve the latest successful build from Jeakins CI Server')
    BUILD_NUM, BUILD_URL = getBuildInfo()
    getLogger().info('Downloading the latest successful build from %s' %(BUILD_URL)) 
    cmd = 'wget -q -T300 ' + BUILD_URL
    runSSHCommand(cmd)

def checkUrl():
    #Sleep for 3 mins and check whether the bsm server is launched.
        getLogger().info('Check whether the build %s is ready for test' %(BUILD_NUM))
        import time,re
        patt = re.compile(r'Login')
        testurl = 'http://' + BSMCONFIG['host_name'] + ':8080/bsm/'
        count = 1
        while(count<=10):
            getLogger().info('Sleep for 10 secs')
            time.sleep(10)
            getLogger().info('Try to access test url: %s' %(testurl))
            html = getHttpResponse(testurl)
            matcher = patt.search(html)
            if matcher is None:
                count += 1
                continue
            else:
                getLogger().info('The build %s is ready for test' %(BUILD_NUM))
                return True
        return False

def runSSHCommand(command):
    getLogger().info('Run ssh command: %s' %(command))
    try:
        stdin, stdout, stderr = SSH.exec_command(command)
        getLogger().info('Std output: %s' %(stdout.read()))
        getLogger().info('Std err: %s' %(stderr.read()))
    except paramiko.SSHException:
        getLogger().error("Error running command: " + command)
        raise    

# Create SFTP connection to upload test data from local path to remote path
def uploadTestData(localpath, remotepath):
    from os import listdir
    from os.path import join
    try:
        getLogger().info('Create SFTP Connection.')
        t = paramiko.Transport((BSMCONFIG['host_name'], 22))
        t.connect(username = BSMCONFIG['host_username'],password = BSMCONFIG['host_password'])
        sftp = paramiko.SFTPClient.from_transport(t)
        getLogger().info('Change working directory to %s' %(remotepath))
        sftp.chdir(remotepath)
        for file in listdir(localpath):
            src = join(localpath, file)
            des = join(remotepath, file)
            sftp.put(src, des)
            getLogger().info('Uploaded file from %s to %s' %(src, des))
        t.close()
    except Exception:
        raise

def deployBuild():    
    getLogger().info('Deploy the latest build on BSM Server')
    try:
        #1. Stop BSM Service
        getLogger().info('Stop BSM Service.')
        cmd = "su -c '$JBOSS_HOME/bin/bsmsrv abort' - bsm"
        runSSHCommand(cmd)
        
        #2. Uninstall previous deployed build
        getLogger().info('Uninstall previous build.')
        cmd = 'rm -rf /soft/bsmsoft/bsm1/bsm'
        runSSHCommand(cmd)
    
        #3. Install BSM component
        build = re.search('_(.*)_', BSMCONFIG['artifact']).group(1)
        getLogger().info('Install BSM package')
        cmd = './nagra_install_3.11.4 bsmsoft bsm1 bsm bsm %s -link yes -peek_tar yes -install yes -overwrite yes -create_dir yes' %(build)
        runSSHCommand(cmd)     
        
        #4. Start BSM Service
        getLogger().info('Launch the BSM Service.')        
        cmd ="su -c '$HOME/bin/bsmsrv_bsm1 start' - bsm"
        runSSHCommand(cmd)       
        
    except Exception:
        raise
    finally:
        SSH.close()

# Sync with P4 Server to get the latest test code
def updateBranch():    
    getLogger().info('Sync with P4 Server to update test branch')
    cmd = 'p4 sync'
    (stdoutput, erroutput) = runCommand(cmd)
    getLogger().info('Result from command: %s' %(stdoutput))
    return stdoutput

def generateReport():
    from genReport import generateRpt
    getLogger().info('Generate Test report')
    generateRpt(BUILD_NUM, BUILD_URL)
    
def sendReport():
    from autoReportSender import sendReport
    getLogger().info('Send out Automation test report')
    sendReport()
    getLogger().info('Finished Automation test on build %s' %(BUILD_NUM))

def runAutoTest():
    getLogger().info('Call Ant Command to run BSM Automation.')
    cmd = 'ant.bat runalltests'
    (stdoutput, erroutput) = runCommand(cmd)
    getLogger().info('Result from Ant command: %s' %(stdoutput))
    return stdoutput

def runCommand(cmd):       
    getLogger().info('Run command %s' %(cmd))
    p = subprocess.Popen(cmd, shell=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    stdoutput, erroutput = p.communicate()
    return stdoutput, erroutput

if __name__ == '__main__':
    global SSH, SSHAS
    global BUILD_NUM, BUILD_URL
    import re, os.path, sys, subprocess
    os.chdir(os.path.abspath(sys.path[0]))
    os.chdir("..")
    BSMCONFIG = getConfigData()
    SSH = getSSHConn()
    BUILD_NUM = ''
    BUILD_URL = ''
    updateBranch()    
    downloadBuild()
    deployBuild()
    if checkUrl():
        runAutoTest()
        generateReport()
        sendReport()
    else :
        getLogger().info('Test url is not avaiable.')
    
                   
                         
        
        
    