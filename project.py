# -*- coding: utf-8 -*-
"""
Created on Sun Sep 20 23:10:32 2020

@author: dhruv
"""


import os.path
import numpy as np
import cv2
import json
from flask import Flask,request,Response
import uuid

def find_sheet_end_point(window):
    window = window.reshape(4,2)
    new_window = np.zeros((4,2),dtype=np.float32)
    
    add = window.sum(axis=1)
    diff = np.diff(window,axis=1)
    
    new_window[0] = window[np.argmin(add)]
    new_window[1] = window[np.argmin(diff)]
    new_window[2] = window[np.argmax(add)]
    new_window[3] = window[np.argmax(diff)]
    
    #new_window*=ratio
    return new_window

def ImageToText(image):
    path_file=('static/%s.jpg'%uuid.uuid4().hex)
    image=cv2.resize(image,(1300,800))
    orig = image
    ratio=image.shape[0]/300.0
    img_grey = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY) 
    img_blur = cv2.GaussianBlur(img_grey,(7,7),0)
    img_edge=cv2.Canny(img_blur,28,48)
   # cv2.imshow('edg', img_edge) 
    
    cnts,hier=cv2.findContours(img_edge,cv2.RETR_LIST,cv2.CHAIN_APPROX_SIMPLE)
    cnts=sorted(cnts,key=cv2.contourArea,reverse=True)
    
    for c in cnts:
        cnt_peri= cv2.arcLength(c,True)
        c_approx=cv2.approxPolyDP(c,0.02*cnt_peri,True)
    
        if len(c_approx)==4:
            selected_window = c_approx
            break
    #cv2.drawContours(image, [selected_window], -1, (0, 255, 0), 3)
    c_approx = find_sheet_end_point(selected_window)
    (tl, tr, br, bl) = c_approx
    
    widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] - bl[1]) ** 2))
    widthB = np.sqrt(((tr[0] - tl[0]) ** 2) + ((tr[1] - tl[1]) ** 2))
    
    heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1] - br[1]) ** 2))
    heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1]) ** 2))
    
    maxWidth = max(int(widthA), int(widthB))
    maxHeight = max(int(heightA), int(heightB))
    
    dst = np.array([
    	[0, 0],
    	[maxWidth - 1, 0],
    	[maxWidth - 1, maxHeight - 1],
    	[0, maxHeight - 1]], dtype = "float32")
    
    M = cv2.getPerspectiveTransform(c_approx, dst)
    warp = cv2.warpPerspective(orig, M, (maxWidth, maxHeight))

   
    cv2.imwrite(path_file,warp)
    return json.dumps(path_file)
app = Flask(__name__)
@app.route("/api/upload",methods=['POST'])



def upload():
    img= cv2.imdecode(np.fromstring(request.files['image'].read(),np.uint8),cv2.IMREAD_UNCHANGED)
    img_processed= ImageToText(img)
    return Response(response=img_processed,status=200,mimetype="application/json")
    
app.run(host="0.0.0.0",port = 80)

if __name__ == "__main__":
    app.run()