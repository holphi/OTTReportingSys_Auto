import logging
import sys

_LOGGERINST = None

def getLogger():
    from time import time,strftime,localtime
    import os.path
    global _LOGGERINST
    if _LOGGERINST is None:
        #SPECIFY A UNIQUE FILE NAME
        filename = os.path.join(sys.path[0],'..','log',strftime('%Y%m%d%H%M%S', localtime(time())))
        _LOGGERINST = logging.getLogger()
        formatter = logging.Formatter('%(asctime)s %(levelname)-12s %(message)s', '%a, %d %b %Y %H:%M:%S',)
        file_handler = logging.FileHandler('%s.log' %(filename))
        file_handler.setFormatter(formatter)
        stream_handler = logging.StreamHandler(sys.stderr)
        stream_handler.setFormatter(formatter)
        _LOGGERINST.addHandler(file_handler)
        _LOGGERINST.addHandler(stream_handler)
        _LOGGERINST.setLevel(logging.INFO)
    return _LOGGERINST

if __name__ == '__main__':
    getLogger().info('This is an info message.')