Feature: operate image by webdev
    Background:
       Given an image processor
       
    Scenario: save an image by webdav
       Given an image is at "http://localhost:8088/webdav/sample_300_300.jpeg"
       And the remote directory to place processed image is at "http://localhost:8088/webdav/result/a/"      
       When I crop the image at (110,140)
       And I save the image as "sample_300_300_110_140.jpeg"
       Then I should see an image is at "http://localhost:8088/webdav/result/a/sample_300_300_110_140.jpeg"
       And the size of image after process is 190,160
       
    Scenario: failed to create missing directory when writting
       Given an image is at "http://localhost:8088/webdav/sample_300_300.jpeg"
       And the remote directory to place processed image is at "http://stresser:8088/result/a/"      
       When I crop the image at (110,140)
       And I save the image as "sample_300_300_110_140.jpeg"
       Then I should see an error message "create the missing directory result"