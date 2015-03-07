from shapely.geometry import Point
from shapely.geometry.polygon import Polygon

class Block:
    def __init__(self, id, boundaries):
        self.id = id
        self.boundaries = boundaries
        self.poly = Polygon(boundaries)

    def __repr__(self):
        return 'Block #' + str(self.id) + ': ' + str(self.boundaries)

    #solves Point-in-Polygon problem for point (lat, lon)
    def contains(self, point):
        s_point = Point(point)
        return self.poly.contains(s_point)
        
