#Some key imports.
#Struct is used to create the actual bytes.
#It is super handy for this type of thing.
import struct, random, copy

# basis_matrix: returns the basis matrix for of order n
def basis_matrix(n):
  # TODO: Generalize to higher dimensions
  return [[0,1],
          [0,1]]

def generate_pixel(bit, party, party_num):
  basis = basis_matrix(party_num)
  row = basis[party]
  if bit == 1: 
    row = [elem ^ 1   for elem in row]

  # FIXME: hardcoded for two-party squaring
  pixel = []
  pixel.append(row[:])
  pixel.append(row[:])
  return pixel

def generate_pixelmap(secret, party, party_num):
  return [[generate_pixel(elem, party, party_num)    for elem in row]     for row in secret]


# collapse_bitmap: takes in a matrix of pixel matrices and collapses into a single matrix
def collapse_bitmap(bitmap):
  collapsed_matrix = []
  for bmp_row in bitmap:
    collapsed_row = []
    collapsed_row.extend(copy.deepcopy(bmp_row[0]))
    for row in range(1, len(bmp_row)):
      for col in range(len(bmp_row[0])):
        collapsed_row[col].extend(copy.deepcopy(bmp_row[row][col]))
    collapsed_matrix += collapsed_row
  return collapsed_matrix

# generate_secret: for testing purposes only
def generate_secret():
  return [[0,0,1,1,0,0],
          [0,1,1,1,1,0],
          [1,1,0,0,1,1],
          [1,1,0,0,1,1],
          [0,1,1,0,1,1],
          [0,0,1,1,0,0]];

# generate_permutations: generate all permutations of n choose m
def generate_permutations(n, m):
  permutations = []
  pass

def random_matrix(row, col):
  return [[random.randint(0,1)   for j in range(col)]    for i in range(row)]

# TODO: test
def matrix_XOR(A, B):
  # TODO: assert the equality between both matrices 
  row = len(A)
  col = len(A[0])
  return [[A[i][j] ^ B[i][j]     for j in range(col)]     for i in range(row)]

def matrix_OR(A, B):
  # TODO: assert the equality between both matrices 
  row = len(A)
  col = len(A[0])
  return [[A[i][j] | B[i][j]     for j in range(col)]     for i in range(row)]

#Function to write a bmp file.  It takes a dictionary (d) of
#header values and the pixel data (bytes) and writes them
#to a file.  This function is called at the bottom of the code.
def bmp_write(d, byte, filename):
  mn1 = struct.pack('<B',d['mn1'])
  mn2 = struct.pack('<B',d['mn2'])
  filesize = struct.pack('<L',d['filesize'])
  undef1 = struct.pack('<H',d['undef1'])
  undef2 = struct.pack('<H',d['undef2'])
  offset = struct.pack('<L',d['offset'])
  headerlength = struct.pack('<L',d['headerlength'])
  width = struct.pack('<L',d['width'])
  height = struct.pack('<L',d['height'])
  colorplanes = struct.pack('<H',d['colorplanes'])
  colordepth = struct.pack('<H',d['colordepth'])
  compression = struct.pack('<L',d['compression'])
  imagesize = struct.pack('<L',d['imagesize'])
  res_hor = struct.pack('<L',d['res_hor'])
  res_vert = struct.pack('<L',d['res_vert'])
  palette = struct.pack('<L',d['palette'])
  importantcolors = struct.pack('<L',d['importantcolors'])
  #create the outfile
  outfile = open(filename + '.bmp','wb')
  #write the header + the bytes
  outfile.write(mn1+mn2+filesize+undef1+undef2+offset+headerlength+width+height+\
                colorplanes+colordepth+compression+imagesize+res_hor+res_vert+\
                palette+importantcolors+byte)
  outfile.close()

def bmp_driver(bmp_matrix, filename):
  #Here is a minimal dictionary with header values.
  #Of importance is the offset, headerlength, width,
  #height and colordepth.
  #Edit the width and height to your liking.
  #These header values are described in the bmp format spec.
  #You can find it on the internet. This is for a Windows
  #Version 3 DIB header.
  d = {
      'mn1':66,
      'mn2':77,
      'filesize':0,
      'undef1':0,
      'undef2':0,
      'offset':54,
      'headerlength':40,
      # 'width':200,
      # 'height':200,
      'colorplanes':0,
      'colordepth':24,
      'compression':0,
      'imagesize':0,
      'res_hor':0,
      'res_vert':0,
      'palette':0,
      'importantcolors':0
      }
  # Width and height are the corresponding col and row numb. in the bitmap matrix
  d['width'] = len(bmp_matrix)
  d['height'] = len(bmp_matrix[0])

  #Function to generate a random number between 0 and 255
  def rand_color():
      x = random.randint(0,1)
      if x == 0:
        return 0
      else:
        return 255

  #Build the byte array.  This code takes the height
  #and width values from the dictionary above and
  #generates the pixels row by row.  The row_mod and padding
  #stuff is necessary to ensure that the byte count for each
  #row is divisible by 4.  This is part of the specification.
  byte = bytes()
  for row in range(d['height']-1,-1,-1):# (BMPs are L to R from the bottom L row)
      for column in range(d['width']):
          b = g = r = 255
          if bmp_matrix[row][column] == 1:
            b = g = r = 0
          pixel = struct.pack('<BBB',b,g,r)
          byte = byte + pixel
      row_mod = (d['width']*d['colordepth']/8) % 4
      if row_mod == 0:
          padding = 0
      else:
          padding = (4 - row_mod)
      padbytes = bytes()
      for i in range(padding):
          x = struct.pack('<B',0)
          padbytes = padbytes + x
      byte = byte + padbytes
      
  #call the bmp_write function with the
  #dictionary of header values and the
  #bytes created above.
  bmp_write(d, byte, filename)


def main():
  secret = generate_secret()
  alpha = random_matrix(len(secret), len(secret[0]))
  beta = matrix_XOR(secret, alpha)
  alpha_pxl = generate_pixelmap(alpha, 0, 2)
  beta_pxl = generate_pixelmap(beta, 1, 2)

  alpha_bmp = collapse_bitmap(alpha_pxl)
  beta_bmp = collapse_bitmap(beta_pxl)
  secret_bmp = matrix_XOR(alpha_bmp, beta_bmp)
  overlayed_bmp = matrix_OR(alpha_bmp, beta_bmp)

  bmp_driver(alpha_bmp,     'alpha')
  bmp_driver(beta_bmp,      'beta')
  bmp_driver(secret_bmp,    'secret')
  bmp_driver(overlayed_bmp, 'overlayed')

if __name__ == '__main__':
  main()

