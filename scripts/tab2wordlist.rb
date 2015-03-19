
File.open(ARGV[0], "r") do |f|
  f.each_line do |line|
    # skip comments
    if line[0] == '#' 
      next
    end   
    # split on tabs
    tok = line.split(/\t+/)       
    puts "\"#{tok[0]}\","    
  end
end