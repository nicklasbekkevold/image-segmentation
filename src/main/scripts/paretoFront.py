import numpy as np
import matplotlib.pyplot as plt

fig = plt.figure()
ax = plt.axes(projection='3d')
ax.set_xlabel('overallDeviation')
ax.set_ylabel('connectivityMeasure')
ax.set_zlabel('edgeValue')

overallDeviation, edgeValue, connectivityMeasure = np.loadtxt('paretoFront.txt', delimiter=",", unpack=True)
ax.scatter3D(overallDeviation, connectivityMeasure, edgeValue)

plt.savefig("paretoFront.png")
plt.show()
plt.close()
