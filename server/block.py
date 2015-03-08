from player import Player
from shapely.geometry import Point
from shapely.geometry.polygon import Polygon

class Block:
    def __init__(self, id, boundaries, center):
        self.id = id
        self.boundaries = boundaries
        self.center = center
        self.poly = Polygon(boundaries)
        self.control = 0.0

    def __repr__(self):
        return 'Block #' + str(self.id) + ': ' + str(self.boundaries)

    #solves Point-in-Polygon problem for point (lat, lon)
    def contains(self, point):
        s_point = Point(point)
        return self.poly.contains(s_point)
        
    #updates control of block toward player's team
    def updateControl(self, player):
        self.control += player.team

        if self.control > 100:
            self.control = 100
        elif self.control < -100:
            self.control = -100
