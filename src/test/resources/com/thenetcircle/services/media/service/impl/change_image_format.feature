Feature: change image format
    Background:
       Given an image processor
       And the directory to place processed image is at "process_result/change_format"
       
    Scenario: from jpeg to png
       Given an image is at "samples/sample_300_300.jpeg"
       When I change the format to "png"
       And I save the image as "sample_300_300_to_png.png"
       Then I should see an image is at "process_result/change_format/sample_300_300_to_png.png"
       And the file format is "png"
       
    Scenario: from png to jpeg
       Given an image is at "samples/sample_png_alpha.png"
       When I change the format to "jpeg"
       And I save the image as "sample_png_alpha_to_jpeg.jpeg"
       Then I should see an image is at "process_result/change_format/sample_png_alpha_to_jpeg.jpeg"
            