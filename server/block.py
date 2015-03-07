class Block:
    def __init__(self, id, boundaries):
        self.id = id
        self.boundaries = boundaries

    def __repr__(self):
        return 'Block #' + str(self.id) + ': ' + str(self.boundaries)

    #solves Point-in-Polygon problem for point (lat, lon)
    #def contains(self, point):
        
