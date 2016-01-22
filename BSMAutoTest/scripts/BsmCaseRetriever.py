'''
Created on Nov 21, 2012
@author: Alex LI
'''

import os, os.path,sys, re, subprocess

class BsmCaseRetriever():

    #Pass the folder where fitnesse.jar resides
    def __init__(self, fitnessePath):
        if not os.path.isabs(fitnessePath):
            self._fitnessePath = os.path.abspath(fitnessePath)
        else:
            self._fitnessePath = fitnessePath
        self._suiteFolderNameLst = os.listdir(os.path.join(self._fitnessePath, 'FitNesseRoot', 'BSMSuite'))
        self._suiteMapping = None

    def getTestSuiteMapping(self):
        if self._suiteMapping is not None:
            return self._suiteMapping
        results = []
        try:
            fileHandler = open(os.path.join(sys.path[0],'TestSuiteMapping.cfg'))
            cfg_items = fileHandler.read().split('\n')
            for item in cfg_items:
                i = item.split('=')
                results.append([i[0],i[1]])
            self._suiteMapping = dict(results)
            return self._suiteMapping
        finally:
            fileHandler.close()
        
    def retrieveCaseDetail(self, automationSuiteName, automationCaseId):
        suiteFolderName = self.getTestSuiteMapping()[automationSuiteName]
        matcher = re.search('.*(\d{4})', automationCaseId)
        if matcher is not None:
            testCaseId = matcher.group(1)
        caseList = os.listdir(os.path.join(self._fitnessePath,'FitNesseRoot','BSMSuite',suiteFolderName))
        caseFolderName = ''
        for caseItem in caseList:
            if caseItem.endswith(testCaseId):
                caseFolderName = caseItem
                break
        contentFilePath = os.path.normpath(os.path.join(self._fitnessePath, 'FitNesseRoot', 'BSMSuite', suiteFolderName, caseFolderName, 'content.txt'))
        if os.path.isfile(contentFilePath):
            fileHandler = open(contentFilePath)
            return fileHandler.read()
        else:
            return ''

    def retrieveCaseTitleAndPriority(self, automationSuiteName, automationCaseId):
        caseDetail = self.retrieveCaseDetail(automationSuiteName, automationCaseId)
        priority = 'P1'
        title = 'NA'
        #Search test case priority
        matcher = re.search('\|Title\s*\|([^\|]*)', caseDetail)
        if matcher is not None:
            title = matcher.group(1).strip()
        matcher = re.search('\|Priority\s*\|([^\|]*)', caseDetail)
        if matcher is not None:
            priority = matcher.group(1).strip()
        return (title, priority, automationSuiteName)

if __name__ == '__main__':
    bsmCaseRetriever = BsmCaseRetriever(r'D:\Perforce\tetang_PLM_NMP_DEV\TEST\MAIN\Fitnesse')
    print bsmCaseRetriever.retrieveCaseDetail('UserMgrTests', 'testUserMgr0100')
    print bsmCaseRetriever.getTestSuiteMapping()