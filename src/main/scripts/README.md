**IT3708 Project 2**

This evaluator tests your ground truth images (white background with black segmentation lines). You can provide either .jpg, .png or a .txt. The .txt file should have the same format as given in the file “example of txt file.txt”: each cell contains a black-white number where 0 is black and 255 is white.

1. Requirements

- Python 2 or 3.

- If your segmentation algorithm provides .png or .jpg, you need to install PIL. If not, remove the import line of PIL.
  - for python 3: python3 -m pip install Pillow
  - for python 2: sudo pip install Pillow
  - Installing on Windows: <https://stackoverflow.com/questions/2088304/installing-pil-python-imaging-library-in-win7-64-bits-python-2-6-4>

- Numpy also needs to be installed.

2. Place the benchmark files in the folder “Optimal\_Segmentation\_Files”. Place your own segmentation files in “Student\_Segmentation\_Files”.

3. Run run.py

PS: Run time is largely affected by the number of black pixels present in your ground truth images.
