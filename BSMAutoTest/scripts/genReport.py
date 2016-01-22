'''
Created on Dec 26, 2012

@author: Alex
'''

import os, sys, re
import HTMLParser
import matplotlib.pyplot as plt
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.mime.image import MIMEImage
from nmplogging import getLogger

OFFSET = 6

class TestDataParser(HTMLParser.HTMLParser):
    
    is_suiteinfo=''
    raw_data = []

    def __init__(self):
        HTMLParser.HTMLParser.__init__(self)
        self.is_suiteinfo = 0
        self.raw_data = []

    def handle_starttag(self, tag, attrs):
        if tag == 'tr':
            for name, value in attrs:
                if (name,value)==('class','Pass') or (name,value)==('class','Failure') or (name, value)==('class','Error'):
                    self.is_suiteinfo = 1

    def handle_endtag(self, tag):
        if tag == 'tr':
            self.is_suiteinfo = 0

    def handle_data(self, data):
        if self.is_suiteinfo==1:
            matcher = re.match('(^\w+$)|(^\d+$)', data)
            if matcher is not None:
                self.raw_data.append(matcher.group())

    def getResultList(self):
        return self.raw_data

    def getSuiteNameList(self):
        result = []
        items = self.getResultList()
        step = 5
        i = 0
        while(i<len(items)):
            result.append(items[i])
            i+=step
        return result

    def getTotalCaseNumList(self):
        result = []
        items = self.getResultList()
        i = 1
        step = 5
        while(i<len(items)):
            result.append(int(items[i]))
            i+=step
        return result
        
    def getFailureNumList(self):
        result = []
        items = self.getResultList()
        i = 2
        step = 5
        while(i<len(items)):
            result.append(int(items[i])+int(items[i+1]))
            i+=step
        return result

class FailureCaseParser(HTMLParser.HTMLParser):

    def __init__(self):
        HTMLParser.HTMLParser.__init__(self)
        self.is_td = 0
        self.failedCaseList = []
        
    def handle_starttag(self, tag, attrs):
        if tag == 'td':
            self.is_td = 1

    def handle_endtag(self, tag):
        if tag == 'td':
            self.is_td = 0

    def handle_data(self, data):
        if self.is_td==1:
            matcher = re.match('test(.*)\d+', data)
            if matcher is not None:
                self.failedCaseList.append(matcher.group())
                
    def getFailureCaseList(self):
        return self.failedCaseList

def loadRptTemplate():
    try:
        getLogger().info('Load BSM report template.')
        fileHandler = open(os.path.join(sys.path[0],'rpt.tpl'))
        return fileHandler.read()
    except Exception, e:
        getLogger().error('Exception happened: %s' %e) 
        raise
    finally:
        fileHandler.close()

def setTestSummaryData(template):
    try:
        getLogger().info('Retrieve test summary data from JUnit report and set it to template') 
        fileHandler = open(os.path.join(sys.path[0],'..','junit', 'overview-summary.html'))
        content = fileHandler.read()
        i = content.index("<h2>Summary</h2>")
        start = content.index("<table", i)
        end = content.index("table>", start) + OFFSET
        table_content = removeLinks(content[start:end])
        return template.replace('#SUMMARY#', table_content)
    except Exception, e:
        getLogger().error('Exception happened: %s' %e) 
        raise
    finally:
        fileHandler.close()

def getDetailTableContent():
    fileHandler = open(os.path.join(sys.path[0],'..','junit', 'com', 'nagra', 'bsm', 'tests', 'package-summary.html'))
    content = fileHandler.read()
    i = content.index("<h2>Classes</h2>")
    start = content.index("<table", i)
    end = content.index("table>", start) + OFFSET
    table_content = removeLinks(content[start:end])
    return table_content
    
def setTestDetailData(template):
    table_content = getDetailTableContent()
    return template.replace('#DETAIL#', table_content)

def removeLinks(html):
    patt = re.compile('href=".*?"')
    return re.sub(patt, '', html)

def setReportDate(template):
    import time
    str_today = time.strftime('%Y/%m/%d',time.localtime(time.time()))
    return template.replace('#TODAY#', str_today)

def drawChartForAutoFailureDistribution():
    getLogger().info('Draw Automation failure distribution chart')
    try:
        table_content = getDetailTableContent()
        testDataParser = TestDataParser()
        testDataParser.feed(table_content.strip())
        getLogger().info('Retrieved Suite Name info from Junit report: %s ' % str(testDataParser.getSuiteNameList()))
        getLogger().info('Retrieved Total number List info from Junit report: %s ' % str(testDataParser.getTotalCaseNumList()))
        getLogger().info('Retrieved Failure number List info from Junit report: %s ' % str(testDataParser.getFailureNumList()))
        font = {'family':'Calibri', 'weight':'normal', 'size':9}
        plt.rc('font', **font)
        plt.figure(figsize = (5, 5))
        plt.pie(x = testDataParser.getFailureNumList(),
                labels = testDataParser.getSuiteNameList(),
                autopct = '%1.1f%%',
                shadow = True)
        plt.title("Automation Failures Distribution", bbox={'facecolor':'0.8', 'pad':5})
        plt.savefig(os.path.join(sys.path[0],'Failure.png'))
    except Exception:
        raise

def retrieveFailedTestCases():
    from BsmCaseRetriever import BsmCaseRetriever
    def getSuiteFailureTableContent(fileName):
        fileHandler = open(fileName)
        content = fileHandler.read()
        i=-99
        try:
            i = content.index("<h2>Errors</h2>")
        except:
            i = content.index("<h2>Failures</h2>")
        start = content.index("<table", i)
        end = content.index("table>", start) + OFFSET
        return content[start:end]
    getLogger().info('Draw Automation Failure chart by prority')
    basePath = os.path.join(sys.path[0],'..', 'junit', 'com', 'nagra', 'bsm', 'tests')
    htmlFileList = os.listdir(basePath)
    bsmCaseRetriever = BsmCaseRetriever(os.path.join(sys.path[0],'.\..\..\Fitnesse'))
    try:
        table_content = getDetailTableContent()
        testDataParser = TestDataParser()
        testDataParser.feed(table_content.strip())
        suiteNameLst = testDataParser.getSuiteNameList()
        getLogger().info(suiteNameLst)
        failureNumLst = testDataParser.getFailureNumList()
        getLogger().info(failureNumLst)
        i = 0
        results = {}
        while(i<len(suiteNameLst)):
            if failureNumLst[i]!=0:
                for fileItem in htmlFileList:
                    pattern = '\d+_%s-errors.html|\d+_%s-fails.html' % (suiteNameLst[i], suiteNameLst[i])
                    if re.match(pattern, fileItem) is not None:
                        failureCaseParser = FailureCaseParser()
                        failureCaseParser.feed(getSuiteFailureTableContent(os.path.join(basePath, fileItem)))
                        for caseId in failureCaseParser.getFailureCaseList():
                            results[caseId] = bsmCaseRetriever.retrieveCaseTitleAndPriority(suiteNameLst[i], caseId)
            i=i+1
        return results
    except Exception:
        raise

def drawChartForAutoFailureByPriority():
    import pylab as p
    results = {'P0':0, 'P1':0, 'P2':0, 'P3':0, 'P4':0, 'P5':0}
    getLogger().info('Draw Automation failures by priorities chart')
    failedCaseDict = retrieveFailedTestCases()
    for key in failedCaseDict:
        priority = failedCaseDict[key][1]
        results[priority]=results[priority]+1
    #Start to draw bar chart
    fig = p.figure(figsize=(5,5))
    ax = fig.add_subplot(1,1,1)
    y = [results['P0'], results['P1'], results['P2'], results['P3'], results['P4'], results['P5']]
    n = len(y)
    ind = range(n)
    ax.bar(ind, y, facecolor='yellow', align='center', ecolor='black')
    ax.set_ylabel('Number of Failures')
    # Create a title, in italics
    ax.set_title('Automation Failures By Priorities', fontstyle='italic')
    ax.set_xticks(ind)
    group_labels = ['P0', 'P1', 'P2', 'P3', 'P4', 'P5']
    ax.set_xticklabels(group_labels)
    fig.autofmt_xdate()
    plt.savefig(os.path.join(sys.path[0],'FailureByPriority.png'))

def setBuildNo(template, buildnum):
    return template.replace('#BUILD_NUM#', buildnum)

def setBuildUrl(template, build_url):
    return template.replace('#BUILD_URL#', build_url)

def generateRpt(build_num, build_url):
    try:
        report = loadRptTemplate()
        report = setTestSummaryData(report)
        report = setTestDetailData(report)
        report = setReportDate(report)
        report = setBuildNo(report, build_num)
        report = setBuildUrl(report, build_url)              
        fileHandler = open(os.path.join(sys.path[0], 'msg.result'), 'w')
        fileHandler.writelines(report)
        fileHandler.close()
        drawChartForAutoFailureDistribution()
        drawChartForAutoFailureByPriority()
    except Exception:
        raise
    finally:
        fileHandler.close()

if __name__ == '__main__':
    generateRpt('308','http://vm81657:8080/jenkins/job/bsm_main/')
    #print drawChartForAutoFailureByPriority()