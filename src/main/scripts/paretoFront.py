import numpy as np
import matplotlib.pyplot as plt

fig = plt.figure()
ax = plt.axes(projection='3d')
ax.set_xlabel('overallDeviation')
ax.set_ylabel('edgeValue')
ax.set_zlabel('connectivityMeasure')

overallDeviation, edgeValue, connectivityMeasure = np.loadtxt('paretoFront.txt', delimiter=",", unpack=True)
ax.scatter3D(overallDeviation, edgeValue, connectivityMeasure)

plt.show()
plt.savefig("paretoFront.png")
plt.close()
